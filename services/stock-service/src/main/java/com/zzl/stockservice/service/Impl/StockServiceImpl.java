package com.zzl.stockservice.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.stockservice.entity.Stock;
import com.zzl.stockservice.exception.StockException;
import com.zzl.stockservice.mapper.StockMapper;
import com.zzl.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 库存服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl extends ServiceImpl<StockMapper, Stock> implements StockService {

    //重复三次
    private static final int RETRY_COUNT = 3;

    /**
     * 下订单时锁库存
     * @param productId 商品ID
     * @param quantity  锁定数量
     * @param orderNo   订单号
     * @return
     */
    @Override
    @Transactional
    public StockLockResult lockStock(Long productId, Integer quantity, String orderNo) {
        for (int i = 0; i < RETRY_COUNT; i++) {

            //获取库存
            Stock stock = lambdaQuery().eq(Stock::getProductId, productId).one();

            if (stock == null) {
                log.warn("商品不存在，productId: {}", productId);
                // 正常业务失败，返回结果，不抛异常
                return buildFailResult(productId, "商品不存在");
            }

            int available = stock.getTotalStock() - stock.getLockedStock() - stock.getSoldStock();
            if (available < quantity) {
                log.warn("库存不足，productId: {}, 可用库存: {}, 请求数量: {}", productId, available, quantity);
                return buildFailResult(productId, "库存不足，剩余可用库存: " + available, available);
            }

            //这个时候锁库存的时候比较Version,即.eq(Stock::getVersion, stock.getVersion())
            //看进程执行期间有没有其他人子在锁库存，当version不相同的时候说明之前有人在进程期间里锁库存
            //抛弃现在的旧数据，重新获取库存信息以便LockedStock是并发进程之间的堆加而不是覆盖
            //同时也是为了获取最新的剩余可使用库存

            //并发优化，直接在数据库中实现locked_stock自增quantity，
            //lockStock 线程 A 读取：locked_stock=100, version=1，准备锁定 50。
            //confirmStock 线程 B 正好在a完成锁库前执行，locked_stock-30=70
            //由于使用旧数据stock.getLockedStock更新，即注入数据库的数据为100+50=150
            //实际应该是120，现在使用直接在数据库中实现locked_stock自增quantity，
            //及放弃了使用stock.getLockedStock旧数据
            // 避免与confirmStock，unlockStock方法的LockedStock加减起冲突
            boolean updated = lambdaUpdate()
                    .eq(Stock::getProductId, productId)
                    .eq(Stock::getVersion, stock.getVersion())
                    .setSql("locked_stock = locked_stock + " + quantity + ", version = version + 1")
                    .update();

            if (updated) {
                log.info("锁定库存成功，productId: {}, quantity: {}, orderNo: {}", productId, quantity, orderNo);
                return buildSuccessResult(productId);
            }
            log.debug("库存锁定乐观锁冲突，重试第{}次", i + 1);
        }

        // 重试耗尽，系统错误，抛异常
        log.error("锁定库存失败，超过重试次数，productId: {}", productId);
        throw new StockException(500, "系统繁忙，请稍后重试");
    }


    /**
     * 用户支付之后减少锁定库存的数量
     * 怎加售出量
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @param orderNo   订单号
     * @return 成功-true
     */
    @Override
    @Transactional
    public boolean confirmStock(Long productId, Integer quantity, String orderNo) {
        boolean updated = lambdaUpdate()
                .eq(Stock::getProductId, productId)
                .setSql("locked_stock = locked_stock - " + quantity + ", sold_stock = sold_stock + " + quantity)
                .update();
        if (updated) {
            log.info("确认扣减成功，productId: {}, quantity: {}, orderNo: {}", productId, quantity, orderNo);
            return true;
        } else {
            // 更新失败（通常因为记录不存在），系统错误，抛异常
            log.error("确认扣减失败，productId: {}, orderNo: {}", productId, orderNo);
            throw new StockException(400, "确认扣减失败，库存记录不存在");
        }
    }

    /**
     * 用户撤销订单
     * 减少锁定库存的数量
     * @param productId 商品ID
     * @param quantity  解锁数量
     * @param orderNo   订单号
     * @return
     */
    @Override
    @Transactional
    public boolean unlockStock(Long productId, Integer quantity, String orderNo) {
        boolean updated = lambdaUpdate()
                .eq(Stock::getProductId, productId)
                .setSql("locked_stock = locked_stock - " + quantity)
                .update();
        if (updated) {
            log.info("解锁库存成功，productId: {}, quantity: {}, orderNo: {}", productId, quantity, orderNo);
            return true;
        } else {
            log.error("解锁库存失败，productId: {}, orderNo: {}", productId, orderNo);
            throw new StockException(400, "解锁库存失败，库存记录不存在");
        }
    }

    @Override
    public Stock getStockByProductId(Long productId) {
        return lambdaQuery().eq(Stock::getProductId, productId).one();
    }


    //在上锁的的时候这里如果返回是null会抛异常，这里就不重复校验有没有返回订单了
    @Override
    public List<Stock> batchGetStock(List<Long> productIds) {
        return lambdaQuery().in(Stock::getProductId, productIds).list();
    }

    private StockLockResult buildFailResult(Long productId, String message) {
        return buildFailResult(productId, message, null);
    }

    private StockLockResult buildFailResult(Long productId, String message, Integer availableStock) {
        StockLockResult result = new StockLockResult();
        result.setSuccess(false);
        result.setMessage(message);
        result.setProductId(productId);
        result.setAvailableStock(availableStock);
        return result;
    }

    private StockLockResult buildSuccessResult(Long productId) {
        StockLockResult result = new StockLockResult();
        result.setSuccess(true);
        result.setMessage("锁定成功");
        result.setProductId(productId);
        return result;
    }
}