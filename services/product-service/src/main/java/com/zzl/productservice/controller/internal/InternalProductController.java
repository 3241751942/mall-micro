package com.zzl.productservice.controller.internal;


import com.zzl.commonapi.dto.product.ProductInternalDTO;
import com.zzl.productservice.converter.ProductConverter;
import com.zzl.productservice.entity.Product;
import com.zzl.productservice.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 内部 Feign 接口（仅供其他微服务调用）
 */
@RestController
@RequestMapping("/internal/products")
@RequiredArgsConstructor
public class InternalProductController {

    private final ProductService productService;

    /**
     * 批量获取商品基本信息
     * @param productIds 商品ID列表
     * @return 商品基本信息列表
     */
    @PostMapping("/batch")
    public List<ProductInternalDTO> batchGetProducts(@RequestBody List<Long> productIds) {
        List<Product> products= productService.batchGetProducts(productIds);
        return ProductConverter.toInternalDTOList(products);
    }
}