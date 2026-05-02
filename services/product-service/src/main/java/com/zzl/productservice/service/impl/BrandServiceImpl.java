package com.zzl.productservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.productservice.Exception.BrandException;
import com.zzl.productservice.entity.Brand;
import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import com.zzl.productservice.mapper.BrandMapper;
import com.zzl.productservice.service.BrandService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 品牌服务实现类
 */
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, Brand> implements BrandService {

    /**
     * 查询所有品牌
     * @return 品牌列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Brand> listBrands() {
        return list();
    }

    /**
     * 根据ID查询品牌
     * @param brandId 品牌ID
     * @return 品牌信息
     */
    @Override
    @Transactional(readOnly = true)
    public Brand getBrandById(Long brandId) {
        Brand brand = getById(brandId);
        if (brand == null) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "品牌不存在，ID：" + brandId);
        }
        return brand;
    }

    /**
     * 删除品牌（逻辑删除）
     * @param brandId 品牌ID
     */
    @Override
    @Transactional
    public void deleteBrand(Long brandId) {
        Brand brand = getById(brandId);
        if (brand == null) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "删除失败，品牌不存在，ID：" + brandId);
        }
        boolean removed = removeById(brandId);
        if (!removed) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "删除品牌失败，ID：" + brandId);
        }
    }

    /**
     * 修改品牌信息
     * @param brandId 品牌ID
     * @param brand   新品牌信息
     */
    @Override
    @Transactional
    public void updateBrand(Long brandId, Brand brand) {
        // 1. 检查原品牌是否存在
        Brand existing = getById(brandId);
        if (existing == null) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "修改失败，品牌不存在，ID：" + brandId);
        }
        // 2. 检查名称是否重复（修改时排除自身）
        if (StringUtils.hasText(brand.getName())) {
            LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Brand::getName, brand.getName())
                    .ne(Brand::getId, brandId);
            if (count(wrapper) > 0) {
                throw new BrandException(ProductServiceBizErrorCode.BRAND_ALREADY_EXISTS, "品牌名称已存在：" + brand.getName());
            }
            existing.setName(brand.getName());
        }
        // 3. 更新非空字段
        if (brand.getLogo() != null) {
            existing.setLogo(brand.getLogo());
        }
        // 4. 执行更新
        boolean updated = updateById(existing);
        if (!updated) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "修改品牌失败，ID：" + brandId);
        }
    }

    /**
     * 新增品牌
     * @param brand 新品牌信息
     */
    @Override
    @Transactional
    public void createBrand(Brand brand) {
        // 校验名称唯一性
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Brand::getName, brand.getName());
        if (count(wrapper) > 0) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_ALREADY_EXISTS, "品牌名称已存在：" + brand.getName());
        }
        boolean saved = save(brand);
        if (!saved) {
            throw new BrandException(ProductServiceBizErrorCode.BRAND_NOT_FOUND, "新增品牌失败");
        }
    }

    /**
     * 分页查询品牌（支持按名称模糊搜索）
     * @param pageNum  当前页码
     * @param pageSize 每页大小
     * @param name     品牌名称（可选，模糊匹配）
     * @return 分页结果
     */
    @Override
    @Transactional(readOnly = true)
    public Page<Brand> pageBrands(Integer pageNum, Integer pageSize, String name) {
        Page<Brand> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Brand> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(name), Brand::getName, name);
        wrapper.orderByAsc(Brand::getId);
        return page(page, wrapper);
    }
}

