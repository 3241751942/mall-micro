package com.zzl.productservice.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.productservice.Exception.BrandException;
import com.zzl.productservice.entity.Brand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
@DisplayName("品牌服务集成测试")
class BrandServiceImplTest {

    @Autowired
    private BrandService brandService;

    private Brand testBrand;
    private final String brandName = "测试品牌";

    @BeforeEach
    void setUp() {
        // 准备一个测试品牌（每次测试前插入，事务结束后回滚）
        testBrand = new Brand();
        testBrand.setName(brandName);
        testBrand.setLogo("http://test.com/logo.png");
        testBrand.setSort(10);
        brandService.createBrand(testBrand);
        // 获取自增ID
        testBrand = brandService.getBrandById(testBrand.getId());
    }

    @Test
    @DisplayName("查询所有品牌 - 成功")
    void testListBrands() {
        List<Brand> brands = brandService.listBrands();
        assertThat(brands).isNotEmpty();
        assertThat(brands).anyMatch(b -> b.getName().equals(brandName));
    }

    @Test
    @DisplayName("根据ID查询品牌 - 成功")
    void testGetBrandByIdSuccess() {
        Brand found = brandService.getBrandById(testBrand.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo(brandName);
        assertThat(found.getLogo()).isEqualTo("http://test.com/logo.png");
    }

    @Test
    @DisplayName("根据ID查询品牌 - 品牌不存在，抛出异常")
    void testGetBrandByIdNotFound() {
        assertThatThrownBy(() -> brandService.getBrandById(99999L))
                .isInstanceOf(BrandException.class)
                .hasMessageContaining("品牌不存在");
    }

    @Test
    @DisplayName("删除品牌 - 成功")
    void testDeleteBrandSuccess() {
        brandService.deleteBrand(testBrand.getId());
        assertThatThrownBy(() -> brandService.getBrandById(testBrand.getId()))
                .isInstanceOf(BrandException.class);
    }

    @Test
    @DisplayName("删除品牌 - 品牌不存在，抛出异常")
    void testDeleteBrandNotFound() {
        assertThatThrownBy(() -> brandService.deleteBrand(99999L))
                .isInstanceOf(BrandException.class)
                .hasMessageContaining("删除失败，品牌不存在");
    }

    @Test
    @DisplayName("修改品牌 - 成功更新名称和Logo")
    void testUpdateBrandSuccess() {
        Brand updateData = new Brand();
        updateData.setName("新测试品牌");
        updateData.setLogo("http://new-logo.com/logo.png");

        brandService.updateBrand(testBrand.getId(), updateData);

        Brand updated = brandService.getBrandById(testBrand.getId());
        assertThat(updated.getName()).isEqualTo("新测试品牌");
        assertThat(updated.getLogo()).isEqualTo("http://new-logo.com/logo.png");
    }

    @Test
    @DisplayName("修改品牌 - 名称重复，抛出异常")
    void testUpdateBrandDuplicateName() {
        // 创建另一个品牌
        Brand another = new Brand();
        another.setName("另一个品牌");
        brandService.createBrand(another);

        // 尝试将测试品牌名称改为另一个品牌的名称
        Brand updateData = new Brand();
        updateData.setName("另一个品牌");
        assertThatThrownBy(() -> brandService.updateBrand(testBrand.getId(), updateData))
                .isInstanceOf(BrandException.class)
                .hasMessageContaining("品牌名称已存在");
    }

    @Test
    @DisplayName("修改品牌 - 品牌不存在，抛出异常")
    void testUpdateBrandNotFound() {
        Brand updateData = new Brand();
        updateData.setName("随便");
        assertThatThrownBy(() -> brandService.updateBrand(99999L, updateData))
                .isInstanceOf(BrandException.class)
                .hasMessageContaining("修改失败，品牌不存在");
    }

    @Test
    @DisplayName("新增品牌 - 成功")
    void testCreateBrandSuccess() {
        Brand newBrand = new Brand();
        newBrand.setName("全新品牌");
        newBrand.setLogo("http://logo.com/new.png");
        brandService.createBrand(newBrand);

        Brand saved = brandService.getBrandById(newBrand.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("全新品牌");
    }

    @Test
    @DisplayName("新增品牌 - 名称重复，抛出异常")
    void testCreateBrandDuplicateName() {
        Brand duplicate = new Brand();
        duplicate.setName(brandName); // 使用已存在的品牌名
        assertThatThrownBy(() -> brandService.createBrand(duplicate))
                .isInstanceOf(BrandException.class)
                .hasMessageContaining("品牌名称已存在");
    }

    @Test
    @DisplayName("分页查询品牌 - 无过滤条件")
    void testPageBrandsWithoutName() {
        Page<Brand> page = brandService.pageBrands(1, 10, null);
        assertThat(page.getTotal()).isGreaterThanOrEqualTo(1);
        assertThat(page.getRecords()).isNotEmpty();
    }

    @Test
    @DisplayName("分页查询品牌 - 按名称模糊搜索")
    void testPageBrandsWithName() {
        Page<Brand> page = brandService.pageBrands(1, 10, "测试");
        assertThat(page.getRecords()).allMatch(b -> b.getName().contains("测试"));
    }

    @Test
    @DisplayName("分页查询品牌 - 搜索不存在的名称")
    void testPageBrandsWithNonexistentName() {
        Page<Brand> page = brandService.pageBrands(1, 10, "不存在的名称");
        assertThat(page.getTotal()).isZero();
        assertThat(page.getRecords()).isEmpty();
    }
}