package com.zzl.cartservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.cartservice.entity.Cart;
import com.zzl.cartservice.entity.CartBO;
import com.zzl.cartservice.entity.CartItem;
import com.zzl.cartservice.exception.CartException;
import com.zzl.cartservice.mapper.CartMapper;
import com.zzl.cartservice.service.CartService;
import com.zzl.commonapi.dto.productservicedto.ProductInternalDTO;
import com.zzl.commonapi.dto.stockservicedto.StockDTO;
import com.zzl.commonapi.feign.productservicefeign.ProductFeignClient;
import com.zzl.commonapi.feign.stockservicefeign.StockFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements CartService {

    private final ProductFeignClient productFeignClient;
    private final StockFeignClient stockFeignClient;

    /**
     * 添加商品到购物车
     * 如果已存在则增加数量，否则新增记录
     *
     * @param userId  用户ID
     * @param request 添加请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCart(Long userId, CartItem request) {


        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, request.getProductId());
        Cart existing = getOne(wrapper);

        if (existing != null) {
            // 已存在：增加数量
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            updateById(existing);
            log.info("更新购物车商品数量，用户ID：{}，商品ID：{}，新数量：{}", userId, request.getProductId(), existing.getQuantity());
        } else {
            // 不存在：新增记录
            Cart newCart = new Cart();
            newCart.setUserId(userId);
            newCart.setProductId(request.getProductId());
            newCart.setQuantity(request.getQuantity());
            newCart.setChecked(1);
            save(newCart);
            log.info("新增购物车记录，用户ID：{}，商品ID：{}，数量：{}", userId, request.getProductId(), request.getQuantity());
        }
    }

    /**
     * 更新购物车商品数量
     * 如果数量<=0则视为删除
     * @param userId    用户ID
     * @param productId 商品ID
     * @param quantity  新数量>=0
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateQuantity(Long userId, Long productId, Integer quantity) {
        Cart cart = getCartItem(userId, productId);

        if (quantity <= 0) {
            removeById(cart.getId());
            log.info("删除购物车商品，用户ID：{}，商品ID：{}", userId, productId);
        } else {
            cart.setQuantity(quantity);
            updateById(cart);
            log.info("更新购物车商品数量，用户ID：{}，商品ID：{}，新数量：{}", userId, productId, quantity);
        }
    }

    /**
     * 删除购物车中的商品
     * @param userId    用户ID
     * @param productId 商品ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeItem(Long userId, Long productId) {
        Cart cart = getCartItem(userId, productId);
        removeById(cart.getId());
        log.info("删除购物车商品，用户ID：{}，商品ID：{}", userId, productId);
    }

    /**
     * 清空用户购物车
     * @param userId 用户ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        remove(wrapper);
        log.info("清空购物车，用户ID：{}", userId);
    }

    /**
     * 更新购物车商品的选中状态
     *
     * @param userId    用户ID
     * @param productId 商品ID
     * @param checked   是否选中
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateChecked(Long userId, Long productId, Boolean checked) {
        Cart cart = getCartItem(userId, productId);
        cart.setChecked(checked ? 1 : 0);
        updateById(cart);
        log.info("更新购物车商品选中状态，用户ID：{}，商品ID：{}，选中：{}", userId, productId, checked);
    }

    /**
     * 全选/全不选
     *
     * @param userId  用户ID
     * @param checked true-全选，false-全不选
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateAllChecked(Long userId, Boolean checked) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);


        List<Cart> cartList = list(wrapper);
        if (CollectionUtils.isEmpty(cartList)) {
            return;
        }
        int checkValue = checked ? 1 : 0;
        cartList.forEach(cart -> cart.setChecked(checkValue));
        updateBatchById(cartList);
        log.info("全选/全不选购物车，用户ID：{}，选中：{}", userId, checked);
    }

    /**
     * 获取用户购物车列表
     *
     * @param userId 用户ID
     * @return 购物车响应数据
     */
    @Override
    @Transactional(readOnly = true)
    public CartBO getCart(Long userId) {
        // 1. 查询该用户所有购物车项
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        List<Cart> cartList = list(wrapper);
        if (CollectionUtils.isEmpty(cartList)) {
            CartBO cartBo = new CartBO();
            cartBo.setItems(List.of());
            cartBo.setTotalAmount(BigDecimal.ZERO);
            cartBo.setTotalChecked(0);
            return cartBo;
        }

        //提取商品ID列表，批量调用商品服务获取商品信息
        List<Long> productIds = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toList());
        //通过商品服务批量获取商品信息
        List<ProductInternalDTO> productInfos = productFeignClient.batchGetProducts(productIds);

        //通过库存服务批量获取商品库存
        List<StockDTO> stockInfos= stockFeignClient.batchQuery(productIds);
        Map<Long, Integer> StockMap = stockInfos
                .stream()
                .collect(Collectors.toMap(StockDTO::getProductId,StockDTO::getAvailableStock));

        // 转为Map
        Map<Long, ProductInternalDTO> productMap = productInfos.stream()
                .collect(Collectors.toMap(ProductInternalDTO::getId, p -> p));

        //组装购物车响应对象
        List<CartItem> items = cartList.stream().map(cart -> {
            Long pid = cart.getProductId();
            ProductInternalDTO info = productMap.get(pid);
            if (info == null) {
                // 商品不存在，可能已下架或删除
                log.warn("购物车中存在无效商品ID：{}，用户ID：{}", pid, userId);
                return null;
            }
            CartItem item = new CartItem();
            item.setProductId(pid);
            item.setProductName(info.getName());
            item.setProductImage(info.getImages());
            item.setPrice(info.getPrice());
            item.setQuantity(cart.getQuantity());
            item.setChecked(cart.getChecked() == 1);
            item.setStock(StockMap.get(pid));
            return item;
        }).filter(Objects::nonNull).collect(Collectors.toList());

        // 4. 计算选中商品总金额和总数量
        BigDecimal totalAmount = items.stream()
                .filter(CartItem::getChecked)
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long totalChecked = items.stream().filter(CartItem::getChecked).count();

        CartBO response = new CartBO();
        response.setItems(items);
        response.setTotalAmount(totalAmount);
        response.setTotalChecked((int) totalChecked);
        return response;
    }

    /**
     * 获取选中商品总金额（供订单服务调用的内部接口）
     *
     * @param userId 用户ID
     * @return 总金额
     */
    @Override
    public BigDecimal getTotalAmount(Long userId) {
        CartBO cart = getCart(userId);
        return cart.getTotalAmount();
    }

    /**
     * 工具方法：根据用户ID和商品ID获取购物车记录
     * 同时验证修改数据的合法性
     * @param userId    用户ID
     * @param productId 商品ID
     * @return 购物车记录
     * @throws CartException 如果记录不存在
     */
    private Cart getCartItem(Long userId, Long productId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart cart = getOne(wrapper);
        if (cart == null) {
            throw new CartException("购物车中该商品不存在");
        }
        return cart;
    }
}