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

    private static final int RETRY_COUNT = 3;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockLockResult lockStock(Long productId, Integer quantity, String orderNo) {
        for (int i = 0; i < RETRY_COUNT; i++) {
            Stock stock = lambdaQuery().eq(Stock::getProductId, productId).one();
            if (stock == null) {
                log.warn("商品不存在，productId: {}", productId);
                // 正常业务失败，返回结果，不抛异常
                return buildFailResult(productId, "商品不存在");
            }

            int available = stock.getTotalStock() - stock.getLockedStock();
            if (available < quantity) {
                log.warn("库存不足，productId: {}, 可用库存: {}, 请求数量: {}", productId, available, quantity);
                return buildFailResult(productId, "库存不足，剩余可用库存: " + available, available);
            }

            boolean updated = lambdaUpdate()
                    .eq(Stock::getProductId, productId)
                    .eq(Stock::getVersion, stock.getVersion())
                    .set(Stock::getLockedStock, stock.getLockedStock() + quantity)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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

    @Override
    @Transactional(rollbackFor = Exception.class)
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