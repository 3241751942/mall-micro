package com.zzl.stockservice.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.stockservice.entity.Stock;
import com.zzl.stockservice.mapper.StockMapper;
import com.zzl.stockservice.service.Impl.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 库存服务并发测试（模拟集群多线程调用）
 * 注意：需要启动真实数据库或使用 H2 嵌入式数据库
 */
@SpringBootTest
@ExtendWith(MockitoExtension.class)
@DisplayName("库存服务并发测试")
public class StockServiceConcurrencyTest {

    @Autowired
    private StockServiceImpl stockService;   // 真实业务实现

    @Autowired
    private StockMapper stockMapper;          // 真实 Mapper，用于验证数据库最终值

    private static final Long PRODUCT_ID = 1001L;
    private static final int TOTAL_STOCK = 100;
    private static final int THREAD_COUNT = 50;
    private static final int REQ_PER_THREAD = 3;   // 每个线程请求3次，总请求150次，但库存只有100

    @BeforeEach
    void setUp() {
        // 初始化库存记录
        Stock stock = new Stock();
        stock.setProductId(PRODUCT_ID);
        stock.setTotalStock(TOTAL_STOCK);
        stock.setLockedStock(0);
        stock.setSoldStock(0);
        stock.setVersion(0);
        // 清理并插入（实际使用 repository 或 mapper）
        stockMapper.delete(new LambdaQueryWrapper<Stock>().eq(Stock::getProductId, PRODUCT_ID));
        stockMapper.insert(stock);
    }

    @Test
    @DisplayName("并发锁定库存 - 乐观锁应保证最终锁定量不超过总库存")
    void testConcurrentLockStock() throws InterruptedException {
        int totalRequests = THREAD_COUNT * REQ_PER_THREAD;
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch latch = new CountDownLatch(totalRequests);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < THREAD_COUNT; i++) {
            executor.submit(() -> {
                for (int j = 0; j < REQ_PER_THREAD; j++) {
                    try {
                        StockLockResult result = stockService.lockStock(PRODUCT_ID, 1, "order_" + System.nanoTime());
                        if (result.isSuccess()) {
                            successCount.incrementAndGet();
                        } else {
                            failCount.incrementAndGet();
                        }
                    } catch (Exception e) {
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }
            });
        }
        latch.await();
        executor.shutdown();

        // 打印统计
        System.out.println("总请求数: " + totalRequests);
        System.out.println("成功锁定次数: " + successCount.get());
        System.out.println("失败次数: " + failCount.get());

        // 验证数据库最终锁定库存量 = 成功锁定的总数量 ≤ 总库存
        Stock finalStock = stockMapper.selectOne(new LambdaQueryWrapper<Stock>().eq(Stock::getProductId, PRODUCT_ID));
        assertThat(finalStock.getLockedStock()).isEqualTo(successCount.get());
        assertThat(finalStock.getLockedStock()).isLessThanOrEqualTo(TOTAL_STOCK);
    }

    @Test
    @DisplayName("并发确认扣减 - 验证锁定库存转为已售库存")
    void testConcurrentConfirmStock() throws InterruptedException {
        // 先锁定 100 个库存（单线程锁定，确保足量）
        for (int i = 0; i < TOTAL_STOCK; i++) {
            stockService.lockStock(PRODUCT_ID, 1, "pre_lock_" + i);
        }

        // 并发确认 100 次
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch latch = new CountDownLatch(TOTAL_STOCK);
        AtomicInteger successConfirm = new AtomicInteger(0);

        for (int i = 0; i < TOTAL_STOCK; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    boolean ok = stockService.confirmStock(PRODUCT_ID, 1, "confirm_" + index);
                    if (ok) successConfirm.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        executor.shutdown();

        Stock finalStock = stockMapper.selectOne(new LambdaQueryWrapper<Stock>().eq(Stock::getProductId, PRODUCT_ID));
        assertThat(finalStock.getLockedStock()).isEqualTo(0);
        assertThat(finalStock.getSoldStock()).isEqualTo(TOTAL_STOCK);
        assertThat(successConfirm.get()).isEqualTo(TOTAL_STOCK);
    }
}