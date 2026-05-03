package com.zzl.stockservice.service;

import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.stockservice.entity.Stock;

import java.util.List;


public interface StockService {

    /**
     * 突然发现注释写在实现类里根本看不到，要写在接口中，逆天
     * 锁定库存（下单时调用）
     *
     * @param productId 商品ID
     * @param quantity  锁定数量
     * @param orderNo   订单号
     * @return 锁定结果
     */
    StockLockResult lockStock(Long productId, Integer quantity, String orderNo);

    /**
     * 确认扣减
     *
     * @param productId 商品ID
     * @param quantity  扣减数量
     * @param orderNo   订单号
     * @return 是否成功
     */
    boolean confirmStock(Long productId, Integer quantity, String orderNo);

    /**
     * 解锁库存
     *
     * @param productId 商品ID
     * @param quantity  解锁数量
     * @param orderNo   订单号
     * @return 是否成功
     */
    boolean unlockStock(Long productId, Integer quantity, String orderNo);

    /**
     * 根据商品ID查询库存信息
     *
     * @param productId 商品ID
     * @return 库存DTO，可能为null
     */
    Stock getStockByProductId(Long productId);

    /**
     * 批量查询库存信息
     *
     * @param productIds 商品ID列表
     * @return 库存DTO列表
     */
    List<Stock> batchGetStock(List<Long> productIds);
}