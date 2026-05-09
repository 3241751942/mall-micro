package com.zzl.cartservice.controller;

import com.zzl.cartservice.converter.CartConverter;
import com.zzl.cartservice.dto.request.AddItemRequest;
import com.zzl.cartservice.dto.request.UpdateCheckedRequest;
import com.zzl.cartservice.dto.request.UpdateQuantityRequest;
import com.zzl.cartservice.dto.response.CartResponse;
import com.zzl.cartservice.entity.CartBO;
import com.zzl.cartservice.service.CartService;
import com.zzl.commoncore.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 购物车前端接口
 */
@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Validated
public class CartController {

    private final CartService cartService;

    /**
     * 添加商品到购物车
     * @param userId  用户ID（从请求头获取）
     * @param request 添加请求（商品ID、数量）
     * @return 成功状态
     */
    @PostMapping("/items")
    public Result<Void> addItem(@RequestHeader("X-User-Id") Long userId,
                                @Valid @RequestBody AddItemRequest request) {
        cartService.addCart(userId, CartConverter.toCart(request));
        return Result.success();
    }

    /**
     * 修改购物车中商品的数量
     * @param userId     用户ID
     * @param productId  商品ID
     * @param request    包含新数量的请求
     * @return 成功状态
     */
    @PutMapping("/items/{productId}")
    public Result<Void> updateQuantity(@RequestHeader("X-User-Id") Long userId,
                                       @PathVariable Long productId,
                                       @Valid @RequestBody UpdateQuantityRequest request) {
        cartService.updateQuantity(userId, productId, request.getQuantity());
        return Result.success();
    }

    /**
     * 删除购物车中的商品
     * @param userId     用户ID
     * @param productId  商品ID
     * @return 成功状态
     */
    @DeleteMapping("/items/{productId}")
    public Result<Void> removeItem(@RequestHeader("X-User-Id") Long userId,
                                   @PathVariable Long productId) {
        cartService.removeItem(userId, productId);
        return Result.success();
    }

    /**
     * 清空购物车
     * @param userId 用户ID
     * @return 成功状态
     */
    @DeleteMapping
    public Result<Void> clearCart(@RequestHeader("X-User-Id") Long userId) {
        cartService.clearCart(userId);
        return Result.success();
    }

    /**
     * 更新购物车商品的选中状态
     * @param userId     用户ID
     * @param productId  商品ID
     * @param request    包含选中状态（true/false）
     * @return 成功状态
     */
    @PutMapping("/items/{productId}/check")
    public Result<Void> updateChecked(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long productId,
                                      @Valid @RequestBody UpdateCheckedRequest request) {
        cartService.updateChecked(userId, productId, request.getChecked());
        return Result.success();
    }

    /**
     * 全选/全不选
     * @param userId  用户ID
     * @param checked true-全选，false-全不选
     * @return 成功状态
     */
    @PutMapping("/check-all")
    public Result<Void> updateAllChecked(@RequestHeader("X-User-Id") Long userId,
                                         @RequestParam Boolean checked) {
        cartService.updateAllChecked(userId, checked);
        return Result.success();
    }

    /**
     * 获取购物车列表（含实时价格、库存）
     * @param userId 用户ID
     * @return 购物车数据
     */
    @GetMapping
    public Result<CartResponse> getCart(@RequestHeader("X-User-Id") Long userId) {
        CartBO cartBO = cartService.getCart(userId);
        CartResponse response = CartConverter.toCartResponse(cartBO);
        return Result.success(response);
    }

    /**
     * 获取选中商品总金额（下单前调用）
     * @param userId 用户ID
     * @return 总金额
     */
    @GetMapping("/total")
    public Result<BigDecimal> getTotalAmount(@RequestHeader("X-User-Id") Long userId) {
        BigDecimal total = cartService.getTotalAmount(userId);
        return Result.success(total);
    }
}