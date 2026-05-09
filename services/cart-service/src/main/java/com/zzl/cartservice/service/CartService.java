package com.zzl.cartservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.cartservice.entity.Cart;
import com.zzl.cartservice.entity.CartBO;
import com.zzl.cartservice.entity.CartItem;

import java.math.BigDecimal;

/**
 * 购物车服务接口
 */
public interface CartService extends IService<Cart> {

    /**
     * 添加商品到购物车
     * @param userId  用户ID
     * @param request 添加请求
     */
    void addCart(Long userId, CartItem request);

    /**
     * 更新购物车中商品的数量
     * @param userId     用户ID
     * @param productId  商品ID
     * @param quantity   新数量
     */
    void updateQuantity(Long userId, Long productId, Integer quantity);

    /**
     * 删除购物车中的商品
     * @param userId     用户ID
     * @param productId  商品ID
     */
    void removeItem(Long userId, Long productId);

    /**
     * 清空用户购物车
     * @param userId 用户ID
     */
    void clearCart(Long userId);

    /**
     * 更新购物车中商品的选中状态
     * @param userId     用户ID
     * @param productId  商品ID
     * @param checked    是否选中
     */
    void updateChecked(Long userId, Long productId, Boolean checked);

    /**
     * 全选/全不选购物车中的商品
     * @param userId  用户ID
     * @param checked true-全选，false-全不选
     */
    void updateAllChecked(Long userId, Boolean checked);

    /**
     * 获取用户购物车列表（包含商品实时价格、库存等信息）
     * @param userId 用户ID
     * @return 购物车响应数据
     */
    CartBO getCart(Long userId);

    /**
     * 获取用户购物车中已选中商品的总金额
     * @param userId 用户ID
     * @return 总金额
     */
    BigDecimal getTotalAmount(Long userId);
}