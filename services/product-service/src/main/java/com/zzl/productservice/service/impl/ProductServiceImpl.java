package com.zzl.productservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.productservice.Exception.ProductException;
import com.zzl.productservice.entity.ProductResponseDTO;
import com.zzl.productservice.dto.request.ProductPageRequest;
import com.zzl.productservice.entity.Product;
import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import com.zzl.productservice.mapper.ProductMapper;
import com.zzl.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final ProductMapper productMapper;


    @Override
    @Transactional(readOnly = true)
    public ProductResponseDTO getDetail(Long productId) {
        ProductResponseDTO productResponseDTO=productMapper.selectProductDetail(productId);
        if(productResponseDTO==null){
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND);
        }
        return productResponseDTO;
    }

    //zhe种集合空的直接返回
    @Override
    @Transactional(readOnly = true)

    @Cacheable(
            value = "productPage",
            key = "#request.pageNum + '_' + #request.pageSize + '_' + " +
                    "#request.categoryId + '_' + #request.brandId + '_' + " +
                    "#request.minPrice + '_' + #request.maxPrice + '_' + " +
                    "#request.keyword + '_' + #request.sortBy + '_' + #request.order",
            unless = "#result == null || #result.records.isEmpty()"
    )
    public Page<ProductResponseDTO> pageQuery(ProductPageRequest request) {

        Page<ProductResponseDTO> page = new Page<>(request.getPageNum(), request.getPageSize());
        return productMapper.selectProductPage(
                page,
                request.getCategoryId(),
                request.getBrandId(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getKeyword(),
                request.getSortBy(),
                request.getOrder()
        );
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createProduct(Product product) {
        // 业务校验：商品名称不能重复（可选）
        long count = lambdaQuery().eq(Product::getName, product.getName()).count();
        if (count > 0) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_ALREADY_EXISTS);
        }
        // 设置默认值（若 Controller 未设置）
        if (product.getStatus() == null) {
            product.setStatus(1); // 默认上架
        }
        if (product.getSales() == null) {
            product.setSales(0);
        }
        boolean saved = save(product);
        if (!saved) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND,"商品添加失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long productId, Product updateInfo) {
        // 检查商品是否存在
        Product existing = getById(productId);
        if (existing == null) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND);
        }
        // 使用 LambdaUpdateWrapper 只更新非空字段，避免覆盖未传字段
        LambdaUpdateWrapper<Product> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Product::getId, productId);
        if (StringUtils.hasText(updateInfo.getName())) {
            wrapper.set(Product::getName, updateInfo.getName());
        }
        if (StringUtils.hasText(updateInfo.getDescription())) {
            wrapper.set(Product::getDescription, updateInfo.getDescription());
        }
        if (updateInfo.getCategoryId() != null) {
            wrapper.set(Product::getCategoryId, updateInfo.getCategoryId());
        }
        if (updateInfo.getBrandId() != null) {
            wrapper.set(Product::getBrandId, updateInfo.getBrandId());
        }
        if (updateInfo.getPrice() != null) {
            wrapper.set(Product::getPrice, updateInfo.getPrice());
        }
        if (updateInfo.getStatus() != null) {
            wrapper.set(Product::getStatus, updateInfo.getStatus());
        }
        if (StringUtils.hasText(updateInfo.getImages())) {
            wrapper.set(Product::getImages, updateInfo.getImages());
        }
        boolean updated = update(wrapper);
        if (!updated) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND,"商品更新失败");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long productId) {
        boolean removed = removeById(productId);
        if (!removed) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND,"商品删除失败");
        }
        // 注意：如果使用逻辑删除，removeById 会自动更新 deleted 字段
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long productId, Integer status) {
        boolean updated = lambdaUpdate()
                .eq(Product::getId, productId)
                .set(Product::getStatus, status)
                .update();
        if (!updated) {
            throw new ProductException(ProductServiceBizErrorCode.PRODUCT_NOT_FOUND,"商品状态修改失败");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> batchGetProducts(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return List.of();
        }
        return lambdaQuery().in(Product::getId, productIds).list();
    }
}