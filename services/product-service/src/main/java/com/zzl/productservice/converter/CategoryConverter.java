package com.zzl.productservice.converter;


import com.zzl.productservice.dto.request.CategoryCreateRequest;
import com.zzl.productservice.dto.request.CategoryUpdateRequest;
import com.zzl.productservice.dto.response.CategoryResponse;
import com.zzl.productservice.entity.Category;
import org.springframework.beans.BeanUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类实体与 DTO 转换器
 */
public class CategoryConverter {

    /**
     * 创建请求 → 实体
     *
     * @param request 创建请求
     * @return 分类实体
     */
    public static Category toEntity(CategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        return category;
    }

    /**
     * 更新请求 → 更新实体
     *
     * @param request  更新请求
     */
    public static Category updateEntity(CategoryUpdateRequest request) {
        if (request == null) {
            return null;
        }
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        return category;
    }

    /**
     * 实体 → 树节点 VO
     *
     * @param category 分类实体
     * @return 分类树节点
     */
    public static CategoryResponse toTreeNode(Category category) {
        if (category == null) {
            return null;
        }
        CategoryResponse node = new CategoryResponse();
        node.setId(category.getId());
        node.setName(category.getName());
        node.setLevel(category.getLevel());
        node.setSort(category.getSort());
        node.setChildren(Collections.emptyList()); // 后续递归填充
        return node;
    }

    /**
     * 实体列表 → 树节点列表
     *
     * @param categories 分类实体列表
     * @return 树节点列表
     */
    public static List<CategoryResponse> toTreeNodeList(List<Category> categories) {
        if (categories == null || categories.isEmpty()) {
            return Collections.emptyList();
        }
        return categories.stream()
                .map(CategoryConverter::toTreeNode)
                .collect(Collectors.toList());
    }
}