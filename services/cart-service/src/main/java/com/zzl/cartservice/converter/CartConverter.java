package com.zzl.cartservice.converter;

import com.zzl.cartservice.dto.request.AddItemRequest;
import com.zzl.cartservice.dto.response.CartItemResponse;
import com.zzl.cartservice.dto.response.CartResponse;
import com.zzl.cartservice.entity.CartBO;
import com.zzl.cartservice.entity.CartItem;

import java.util.stream.Collectors;

/**
 * 购物车转换器（DTO ↔ 实体 ↔ BO）
 */
public class CartConverter {

    /**
     * 将内部业务对象 CartBO -> 响应对象 CartResponse
     * @param cartBO 内部购物车业务对象
     * @return 前端响应对象
     */
    public static CartResponse toCartResponse(CartBO cartBO) {
        if (cartBO == null) {
            return null;
        }
        CartResponse response = new CartResponse();
        response.setTotalAmount(cartBO.getTotalAmount());
        response.setTotalChecked(cartBO.getTotalChecked());
        if (cartBO.getItems() != null) {
            response.setItems(cartBO.getItems().stream()
                    .map(CartConverter::toCartItemResponse)
                    .collect(Collectors.toList()));
        }
        return response;
    }

    /**
     * 将内部业务单项 BO -> API 单项响应
     * @param itemBO 内部单项 BO
     * @return 响应单项
     */
    private static CartItemResponse toCartItemResponse(CartItem itemBO) {
        if (itemBO == null) {
            return null;
        }
        CartItemResponse response = new CartItemResponse();
        response.setProductId(itemBO.getProductId());
        response.setProductName(itemBO.getProductName());
        response.setProductImage(itemBO.getProductImage());
        response.setPrice(itemBO.getPrice());
        response.setQuantity(itemBO.getQuantity());
        response.setChecked(itemBO.getChecked());
        response.setStock(itemBO.getStock());
        return response;
    }

    /**
     * 将添加api购物车请求 DTO -> 实体 Cart
     * @param request  添加请求
     * @return 购物车实体,只有商品id和客户id,以及商品数量
     */
    public static CartItem toCart(AddItemRequest request) {
        if (request == null) {
            return null;
        }
        CartItem cart = new CartItem();
        cart.setProductId(request.getProductId());
        cart.setQuantity(request.getQuantity());
        // 默认选中
        cart.setChecked(true);
        return cart;
    }
}