package com.zzl.productservice.controller.admin;

import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.ProductConverter;
import com.zzl.productservice.dto.request.ProductCreateRequest;
import com.zzl.productservice.dto.request.ProductUpdateRequest;
import com.zzl.productservice.entity.Product;
import com.zzl.productservice.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 商品管理端接口
 */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
@Validated
public class AdminProductController {

    private final ProductService productService;

    /**
     * 新增商品
     *
     * @param request 商品信息（名称、分类、价格等）
     * @return 无数据，成功状态码
     */
    @PostMapping
    public Result<Void> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Product product= ProductConverter.toEntity(request);
        productService.createProduct(product);
        return Result.success();
    }

    /**
     * 修改商品
     *
     * @param productId 商品ID
     * @param request   需要更新的商品字段
     * @return 无数据
     */
    @PutMapping("/{productId}")
    public Result<Void> updateProduct(@PathVariable Long productId,
                                      @Valid @RequestBody ProductUpdateRequest request) {
        Product product= ProductConverter.toUpdateEntity(request);
        productService.updateProduct(productId, product);
        return Result.success();
    }

    /**
     * 删除商品（逻辑删除）
     *
     * @param productId 商品ID
     * @return 无数据
     */
    @DeleteMapping("/{productId}")
    public Result<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return Result.success();
    }

    /**
     * 上下架商品
     *
     * @param productId 商品ID
     * @param status    状态：0-下架，1-上架
     * @return 无数据
     */
    @PutMapping("/{productId}/status")
    public Result<Void> changeStatus(@PathVariable Long productId,
                                     @RequestParam @NotNull(message = "状态不能为空") @Min(0) @Max(1) Integer status) {
        productService.changeStatus(productId, status);
        return Result.success();
    }
}