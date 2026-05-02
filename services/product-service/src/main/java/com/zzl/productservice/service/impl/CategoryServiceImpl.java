package com.zzl.productservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.productservice.Exception.CategoryException;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import com.zzl.productservice.mapper.CategoryMapper;
import com.zzl.productservice.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 获取整个分类树
     */
    @Override
    @Transactional(readOnly = true)
    public List<Category> getCategoryTree() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort, Category::getId);
        return list(wrapper);
    }

    /**
     * 新增分类
     * @param category 分类信息
     */
    @Override
    @Transactional
    public void createCategory(Category category) {
        // 校验父级是否存在（如果 parentId 不是 0）
        if (category.getParentId() != null && category.getParentId() != 0) {
            Category parent = getById(category.getParentId());
            if (parent == null) {
                throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "父级分类不存在，ID：" + category.getParentId());
            }
        }
        // 校验同级下名称是否重复
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, category.getParentId() == null ? 0 : category.getParentId())
                .eq(Category::getName, category.getName());
        if (count(wrapper) > 0) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_ALREADY_EXISTS,
                    "同级分类下名称已存在：" + category.getName());
        }
        // 自动设置 level（根据父级level+1）
        if (category.getParentId() == null || category.getParentId() == 0) {
            category.setLevel(1);
        } else {
            Category parent = getById(category.getParentId());
            if (parent != null) {
                category.setLevel(parent.getLevel() + 1);
            } else {
                category.setLevel(1);
            }
        }
        boolean saved = save(category);
        if (!saved) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "新增分类失败");
        }
    }

    /**
     * 修改分类
     * @param categoryId 分类ID
     * @param category   新的分类信
     */
    @Override
    @Transactional
    public void updateCategory(Long categoryId, Category category) {
        Category existing = getById(categoryId);
        if (existing == null) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "修改失败，分类不存在，ID：" + categoryId);
        }
        // 如果修改名称，检查同级是否重复
        if (StringUtils.hasText(category.getName()) && !category.getName().equals(existing.getName())) {
            LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Category::getParentId, existing.getParentId())
                    .eq(Category::getName, category.getName())
                    .ne(Category::getId, categoryId);
            if (count(wrapper) > 0) {
                throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_ALREADY_EXISTS,
                        "同级分类下名称已存在：" + category.getName());
            }
            existing.setName(category.getName());
        }
        // 修改其他字段
        if (category.getParentId() != null && !category.getParentId().equals(existing.getParentId())) {
            // 改变父级，需要重新计算 level
            existing.setParentId(category.getParentId());
            if (category.getParentId() == 0) {
                existing.setLevel(1);
            } else {
                Category newParent = getById(category.getParentId());
                if (newParent == null) {
                    throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "新父级分类不存在");
                }
                existing.setLevel(newParent.getLevel() + 1);
            }
        }
        if (category.getSort() != null) {
            existing.setSort(category.getSort());
        }
        boolean updated = updateById(existing);
        if (!updated) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "修改分类失败，ID：" + categoryId);
        }
    }

    /**
     * 删除分类（逻辑删除）
     * @param categoryId 分类ID
     */
    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getById(categoryId);
        if (category == null) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "删除失败，分类不存在，ID：" + categoryId);
        }
        // 检查是否有子分类（可选业务规则）
        LambdaQueryWrapper<Category> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(Category::getParentId, categoryId);
        if (count(childWrapper) > 0) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "请先删除子分类");
        }
        boolean removed = removeById(categoryId);
        if (!removed) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "删除分类失败，ID：" + categoryId);
        }
    }

    /**
     * 根据ID查询分类
     * @param categoryId 分类ID
     * @return 分类信息
     */
    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(Long categoryId) {
        Category category = getById(categoryId);
        if (category == null) {
            throw new CategoryException(ProductServiceBizErrorCode.CATEGORY_NOT_FOUND, "分类不存在，ID：" + categoryId);
        }
        return category;
    }
}