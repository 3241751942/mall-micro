package com.zzl.productservice.converter;

import com.zzl.commonapi.dto.product.ProductInternalDTO;
import com.zzl.productservice.entity.ProductResponseDTO;
import com.zzl.productservice.dto.request.ProductCreateRequest;
import com.zzl.productservice.dto.request.ProductUpdateRequest;
import com.zzl.productservice.dto.response.ProductResponse;
import com.zzl.productservice.entity.Product;
import org.springframework.beans.BeanUtils;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品实体与 DTO 转换器
 */
public class ProductConverter {

    /**
     * 创建请求 → 实体
     * @param request 创建请求
     * @return 商品实体（未包含 id, status, sales, 时间字段）
     */
    public static Product toEntity(ProductCreateRequest request) {
        if (request == null) {
            return null;
        }
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        product.setStatus(1);      // 默认上架
        product.setSales(0);       // 初始销量 0
        return product;
    }

    /**
     * 更新请求 → 实体
     * @param request 更新请求
     * @return 商品实体
     */
    public static Product toUpdateEntity(ProductUpdateRequest request) {
        if (request == null) {
            return null;
        }
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        return product;
    }


    /**
     * 实体 → 对外响应
     */
    public static ProductResponse toProductResponse(ProductResponseDTO productResponseDTO) {
        ProductResponse vo = new ProductResponse();
        BeanUtils.copyProperties(productResponseDTO, vo);
        if (productResponseDTO.getCreateTime() != null) {
            vo.setCreateTime(productResponseDTO.getCreateTime());
        }
        return vo;
    }

    /**
     * 实体 → 内部传输 DTO（供订单等微服务使用）
     *
     * @param product 商品实体
     * @return 内部 DTO
     */
    public static ProductInternalDTO toInternalDTO(Product product) {
        if (product == null) {
            return null;
        }
        ProductInternalDTO dto = new ProductInternalDTO();
        BeanUtils.copyProperties(product, dto);
        return dto;
    }

    /**
     * 实体列表 → 内部 DTO 列表
     *
     * @param products 商品实体列表
     * @return 内部 DTO 列表
     */
    public static List<ProductInternalDTO> toInternalDTOList(List<Product> products) {
        if (products == null || products.isEmpty()) {
            return Collections.emptyList();
        }
        return products.stream()
                .map(ProductConverter::toInternalDTO)
                .collect(Collectors.toList());
    }
}
