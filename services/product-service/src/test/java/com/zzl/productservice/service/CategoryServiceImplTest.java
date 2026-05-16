package com.zzl.productservice.service;


import com.zzl.productservice.Exception.CategoryException;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.entity.CategoryTree;
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
@DisplayName("分类服务集成测试")
class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    private Category parentCategory;
    private Category childCategory;

    @BeforeEach
    void setUp() {
        // 创建一级分类（父级）
        parentCategory = new Category();
        parentCategory.setParentId(0L);
        parentCategory.setName("电子产品");
        parentCategory.setLevel(1);
        parentCategory.setSort(1);
        categoryService.createCategory(parentCategory);
        // 获取自增ID
        parentCategory = categoryService.getCategoryById(parentCategory.getId());

        // 创建二级分类（子级）
        childCategory = new Category();
        childCategory.setParentId(parentCategory.getId());
        childCategory.setName("手机");
        childCategory.setLevel(2);
        childCategory.setSort(1);
        categoryService.createCategory(childCategory);
        childCategory = categoryService.getCategoryById(childCategory.getId());
    }

    @Test
    @DisplayName("获取分类树 - 返回所有分类并按排序和ID排序")
    void testGetCategoryTree() {
        List<CategoryTree> tree = categoryService.getCategoryTree();
        assertThat(tree).hasSize(2);
        // 验证排序：sort小的在前，相同sort则id小的在前
        assertThat(tree.get(0).getName()).isEqualTo("电子产品");
        assertThat(tree.get(1).getName()).isEqualTo("手机");
    }

    @Test
    @DisplayName("新增分类 - 成功创建一级分类")
    void testCreateCategoryTopLevelSuccess() {
        Category newTop = new Category();
        newTop.setParentId(0L);
        newTop.setName("家用电器");
        categoryService.createCategory(newTop);

        Category saved = categoryService.getCategoryById(newTop.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("家用电器");
        assertThat(saved.getLevel()).isEqualTo(1);
    }

    @Test
    @DisplayName("新增分类 - 成功创建二级分类")
    void testCreateCategoryChildSuccess() {
        Category child = new Category();
        child.setParentId(parentCategory.getId());
        child.setName("平板电脑");
        categoryService.createCategory(child);

        Category saved = categoryService.getCategoryById(child.getId());
        assertThat(saved.getLevel()).isEqualTo(2);
        assertThat(saved.getParentId()).isEqualTo(parentCategory.getId());
    }

    @Test
    @DisplayName("新增分类 - 父级分类不存在，抛出异常")
    void testCreateCategoryParentNotFound() {
        Category invalid = new Category();
        invalid.setParentId(99999L);
        invalid.setName("无效子分类");
        assertThatThrownBy(() -> categoryService.createCategory(invalid))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("父级分类不存在");
    }

    @Test
    @DisplayName("新增分类 - 同级分类名称重复，抛出异常")
    void testCreateCategoryDuplicateName() {
        Category duplicate = new Category();
        duplicate.setParentId(parentCategory.getId());
        duplicate.setName("手机"); // 与已存在的子分类同名
        assertThatThrownBy(() -> categoryService.createCategory(duplicate))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("同级分类下名称已存在");
    }

    @Test
    @DisplayName("修改分类 - 成功更新名称")
    void testUpdateCategoryNameSuccess() {
        Category update = new Category();
        update.setName("智能手机");
        categoryService.updateCategory(childCategory.getId(), update);

        Category updated = categoryService.getCategoryById(childCategory.getId());
        assertThat(updated.getName()).isEqualTo("智能手机");
    }

    @Test
    @DisplayName("修改分类 - 成功更换父级（并重新计算层级）")
    void testUpdateCategoryParentSuccess() {
        // 新建另一个一级分类作为新父级
        Category newParent = new Category();
        newParent.setParentId(0L);
        newParent.setName("数码配件");
        categoryService.createCategory(newParent);

        Category update = new Category();
        update.setParentId(newParent.getId());
        categoryService.updateCategory(childCategory.getId(), update);

        Category updated = categoryService.getCategoryById(childCategory.getId());
        assertThat(updated.getParentId()).isEqualTo(newParent.getId());
        assertThat(updated.getLevel()).isEqualTo(2); // 原二级变为新父级下的二级，还是2级
    }

    @Test
    @DisplayName("修改分类 - 成功修改排序")
    void testUpdateCategorySortSuccess() {
        Category update = new Category();
        update.setSort(100);
        categoryService.updateCategory(childCategory.getId(), update);

        Category updated = categoryService.getCategoryById(childCategory.getId());
        assertThat(updated.getSort()).isEqualTo(100);
    }

    @Test
    @DisplayName("修改分类 - 同级名称重复，抛出异常")
    void testUpdateCategoryDuplicateName() {
        // 创建另一个三级分类（避免与手机冲突）
        Category another = new Category();
        another.setParentId(parentCategory.getId());
        another.setName("笔记本电脑");
        categoryService.createCategory(another);

        Category update = new Category();
        update.setName("笔记本电脑");
        assertThatThrownBy(() -> categoryService.updateCategory(childCategory.getId(), update))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("同级分类下名称已存在");
    }

    @Test
    @DisplayName("修改分类 - 目标分类不存在，抛出异常")
    void testUpdateCategoryNotFound() {
        Category update = new Category();
        update.setName("随便");
        assertThatThrownBy(() -> categoryService.updateCategory(99999L, update))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("修改失败，分类不存在");
    }

    @Test
    @DisplayName("修改分类 - 新父级不存在，抛出异常")
    void testUpdateCategoryNewParentNotFound() {
        Category update = new Category();
        update.setParentId(88888L);
        assertThatThrownBy(() -> categoryService.updateCategory(childCategory.getId(), update))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("新父级分类不存在");
    }

    @Test
    @DisplayName("删除分类 - 成功删除叶子分类")
    void testDeleteCategoryLeafSuccess() {
        categoryService.deleteCategory(childCategory.getId());
        assertThatThrownBy(() -> categoryService.getCategoryById(childCategory.getId()))
                .isInstanceOf(CategoryException.class);
    }

    @Test
    @DisplayName("删除分类 - 存在子分类，抛出异常")
    void testDeleteCategoryHasChildren() {
        assertThatThrownBy(() -> categoryService.deleteCategory(parentCategory.getId()))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("请先删除子分类");
    }

    @Test
    @DisplayName("删除分类 - 分类不存在，抛出异常")
    void testDeleteCategoryNotFound() {
        assertThatThrownBy(() -> categoryService.deleteCategory(99999L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("删除失败，分类不存在");
    }

    @Test
    @DisplayName("根据ID查询分类 - 存在")
    void testGetCategoryByIdExists() {
        Category found = categoryService.getCategoryById(parentCategory.getId());
        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("电子产品");
    }

    @Test
    @DisplayName("根据ID查询分类 - 不存在")
    void testGetCategoryByIdNotExists() {
        assertThatThrownBy(() -> categoryService.getCategoryById(99999L))
                .isInstanceOf(CategoryException.class)
                .hasMessageContaining("分类不存在");
    }
}
