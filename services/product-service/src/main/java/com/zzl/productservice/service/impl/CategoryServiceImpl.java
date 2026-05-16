package com.zzl.productservice.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.productservice.Exception.CategoryException;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.entity.CategoryTree;
import com.zzl.productservice.enums.ProductServiceBizErrorCode;
import com.zzl.productservice.mapper.CategoryMapper;
import com.zzl.productservice.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类服务实现类
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    /**
     * 获取整个分类树(最多三层)
     */
    public List<CategoryTree> getCategoryTree() {
        // 1. 查询所有未删除、1-3级分类，并按排序号升序
        List<Category> categoryList = lambdaQuery()
                .in(Category::getLevel, 1, 2, 3)
                .eq(Category::getDeleted, 0)
                .orderByAsc(Category::getSort)
                .list();

        // 2. 构建树形结构
        return buildCategoryTree(categoryList);
    //remove()
        //update()
        //list()
        //getOne()
        //save()
    }

    /**
     * 核心：扁平列表 → 三级 CategoryTree 树形结构
     */
    private List<CategoryTree> buildCategoryTree(List<Category> categoryList) {
        // 第一步：把所有 Category 转成 CategoryTree
        List<CategoryTree> allNodes = categoryList.stream().map(cat -> {
            CategoryTree treeNode = new CategoryTree();
            treeNode.setId(cat.getId());
            treeNode.setName(cat.getName());
            treeNode.setLevel(cat.getLevel());
            treeNode.setSort(cat.getSort());
            treeNode.setChildren(new ArrayList<>());
            return treeNode;
        }).toList();

        // 建立 ID -> 节点 的映射
        Map<Long, CategoryTree> nodeMap = allNodes.stream()
                .collect(Collectors.toMap(CategoryTree::getId, node -> node));

        List<CategoryTree> resultTree = new ArrayList<>();

        // 遍历组装父子关系
        for (CategoryTree node : allNodes) {
            // 找到当前节点对应的原始分类数据
            Category currentCat = categoryList.stream()
                    .filter(c -> c.getId().equals(node.getId()))
                    .findFirst().orElse(null);

            if (currentCat == null) continue;

            Long parentId = currentCat.getParentId();

            // parentId = 0 或 null → 一级分类
            if (parentId == null || parentId == 0) {
                resultTree.add(node);
            } else {
                // 找到父节点，把自己挂进去
                CategoryTree parentNode = nodeMap.get(parentId);
                if (parentNode != null) {
                    parentNode.getChildren().add(node);
                }
            }
        }
        return resultTree;
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