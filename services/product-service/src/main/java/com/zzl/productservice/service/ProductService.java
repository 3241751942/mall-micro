package com.zzl.productservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.productservice.entity.ProductResponseDTO;
import com.zzl.productservice.dto.request.ProductPageRequest;
import com.zzl.productservice.entity.Product;
import jakarta.validation.Valid;

import java.util.List;

public interface ProductService extends IService<Product> {
    ProductResponseDTO getDetail(Long productId);

    void createProduct(Product product);

    void updateProduct(Long productId,Product product);

    void deleteProduct(Long productId);

    void changeStatus(Long productId,Integer status);

    List<Product> batchGetProducts(List<Long> productIds);

    Page<ProductResponseDTO> pageQuery(ProductPageRequest request);
}
