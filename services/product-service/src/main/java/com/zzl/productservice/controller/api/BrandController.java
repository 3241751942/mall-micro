package com.zzl.productservice.controller.api;

import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.BrandConverter;
import com.zzl.productservice.dto.response.BrandResponse;
import com.zzl.productservice.entity.Brand;
import com.zzl.productservice.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 品牌对外接口
 */
@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {

    private final BrandService brandService;

    /**
     * 获取所有品牌列表
     * @return 品牌列表（包含ID、名称、Logo）
     */
    @GetMapping
    public Result<List<BrandResponse>> listBrands() {
        List<Brand> brands= brandService.listBrands();

        return Result.success(BrandConverter.toBrandResponseList(brands));
    }
}