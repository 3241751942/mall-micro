package com.zzl.productservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.productservice.entity.Brand;
import java.util.List;

public interface BrandService extends IService<Brand> {
    List<Brand> listBrands();

    Brand getBrandById(Long brandId);

    void deleteBrand(Long brandId);

    void updateBrand(Long brandId,Brand brand);

    void createBrand(Brand brand);

    Page<Brand> pageBrands(Integer pageNum, Integer pageSize, String name);
}
