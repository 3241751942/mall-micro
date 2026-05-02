package com.zzl.productservice.converter;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.productservice.dto.request.BrandCreateRequest;
import com.zzl.productservice.dto.request.BrandUpdateRequest;
import com.zzl.productservice.dto.response.BrandResponse;
import com.zzl.productservice.entity.Brand;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 品牌实体与 DTO 转换器
 *
 * @author micro
 * @since 1.0.0
 */
public class BrandConverter {

    /**
     * 创建请求 → 实体
     *
     * @param request 创建请求
     * @return 品牌实体
     */
    public static Brand toEntity(BrandCreateRequest request) {
        if (request == null) {
            return null;
        }
        Brand brand = new Brand();
        BeanUtils.copyProperties(request, brand);
        return brand;
    }

    /**
     * 更新请求 → 更新实体（非空字段覆盖）
     *
     * @param request 更新请求
     */
    public static Brand updateEntity(BrandUpdateRequest request) {
        Brand brand = new Brand();
        BeanUtils.copyProperties(request, brand);
        return brand;

    }

    /**
     * 实体 → 对外 VO
     *
     * @param brand 品牌实体
     * @return 品牌 VO
     */
    public static BrandResponse toBrandResponse(Brand brand) {
        if (brand == null) {
            return null;
        }
        BrandResponse vo = new BrandResponse();
        BeanUtils.copyProperties(brand, vo);
        return vo;
    }

    /**
     * 实体列表 → VO 列表
     *
     * @param brands 品牌实体列表
     * @return 品牌 VO 列表
     */
    public static List<BrandResponse> toBrandResponseList(List<Brand> brands) {
        if (brands == null || brands.isEmpty()) {
            return Collections.emptyList();
        }
        return brands.stream()
                .map(BrandConverter::toBrandResponse)
                .collect(Collectors.toList());
    }

    /**
     * 分页实体 → 分页 VO
     *
     * @param page 分页实体
     * @return 分页 VO
     */
    public static Page<BrandResponse> toBrandResponsePage(Page<Brand> page) {
        Page<BrandResponse> voPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        if (page.getRecords() != null) {
            List<BrandResponse> voList = toBrandResponseList(page.getRecords());
            voPage.setRecords(voList);
        }
        return voPage;
    }
}