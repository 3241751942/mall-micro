package com.zzl.productservice.controller.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.ProductConverter;
import com.zzl.productservice.entity.ProductResponseDTO;
import com.zzl.productservice.dto.request.ProductPageRequest;
import com.zzl.productservice.dto.response.ProductResponse;
import com.zzl.productservice.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品对外接口
 */
@Slf4j
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Validated
public class ProductController {

    private final ProductService productService;

    /**
     * 分页查询商品列表
     * @param request 分页请求参数（页码、每页条数、筛选条件、排序字段）
     * @return 分页商品数据，包含商品简要信息及分类名称、品牌名称
     */
    @GetMapping("/page")
    public Result<Page<ProductResponse>> pageQuery(@Valid ProductPageRequest request) {
        Page<ProductResponseDTO> page= productService.pageQuery(request);
        Page<ProductResponse> resultPage =new Page<>(
                page.getCurrent(),
                page.getTotal(),
                page.getSize()
        );

        resultPage.setRecords(
                page.getRecords()
                        .stream()
                        .map(ProductConverter::toProductResponse)
                        .toList());
        return Result.success(resultPage);
    }

    /**
     * 获取商品详情
     * @param productId 商品ID
     * @return 商品详细信息
     */
    @GetMapping("/{productId}")
    public Result<ProductResponse> getDetail(@PathVariable Long productId) {
        ProductResponseDTO productResponseDTO=productService.getDetail(productId);
        return Result.success(ProductConverter.toProductResponse(productResponseDTO));
    }
}