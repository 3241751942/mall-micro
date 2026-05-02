package com.zzl.productservice.controller.admin;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.BrandConverter;
import com.zzl.productservice.dto.request.BrandCreateRequest;
import com.zzl.productservice.dto.request.BrandUpdateRequest;
import com.zzl.productservice.dto.response.BrandResponse;
import com.zzl.productservice.entity.Brand;
import com.zzl.productservice.service.BrandService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



/**
 * 品牌管理端接口
 */
@RestController
@RequestMapping("/api/admin/brands")
@RequiredArgsConstructor
@Validated
public class AdminBrandController {

    private final BrandService brandService;

    /**
     * 新增品牌
     *
     * @param request 品牌信息（名称、Logo、排序）
     * @return 成功响应
     */
    @PostMapping
    public Result<Void> createBrand(@Valid @RequestBody BrandCreateRequest request) {
        Brand brand =BrandConverter.toEntity(request);
        brandService.createBrand(brand);
        return Result.success();
    }

    /**
     * 修改品牌
     *
     * @param brandId 品牌ID
     * @param request 需要修改的字段
     * @return 成功响应
     */
    @PutMapping("/{brandId}")
    public Result<Void> updateBrand(@PathVariable Long brandId,
                                    @Valid @RequestBody BrandUpdateRequest request) {
        Brand brand=BrandConverter.updateEntity(request);
        brandService.updateBrand(brandId, brand);
        return Result.success();
    }

    /**
     * 删除品牌（逻辑删除）
     * @param brandId 品牌ID
     * @return 成功响应
     */
    @DeleteMapping("/{brandId}")
    public Result<Void> deleteBrand(@PathVariable Long brandId) {
        brandService.deleteBrand(brandId);
        return Result.success();
    }

    /**
     * 查询品牌详情（用于编辑回显）
     * @param brandId 品牌ID
     * @return 品牌详细信息
     */
    @GetMapping("/{brandId}")
    public Result<BrandResponse> getBrandById(@PathVariable @NotNull Long brandId) {
        Brand brand= brandService.getBrandById(brandId);
        return Result.success(BrandConverter.toBrandResponse(brand));
    }

    /**
     * 分页查询品牌（管理端列表）
     *
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @param name     品牌名称（可选模糊查询）
     * @return 分页品牌数据
     */
    @GetMapping("/page")
    public Result<Page<BrandResponse>> pageBrands(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String name) {
        Page<Brand> page = brandService.pageBrands(pageNum, pageSize, name);

        Page<BrandResponse> resultPage = new Page<>(
                page.getCurrent(),
                page.getSize(),
                page.getTotal()
        );

        resultPage.setRecords(
                page.getRecords().stream()
                        .map(BrandConverter::toBrandResponse)
                        .toList()
        );
        return Result.success(resultPage);
    }
}
