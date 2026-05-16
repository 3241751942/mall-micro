package com.zzl.productservice.converter;


import com.zzl.productservice.dto.request.CategoryCreateRequest;
import com.zzl.productservice.dto.request.CategoryUpdateRequest;
import com.zzl.productservice.dto.response.CategoryResponse;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.entity.CategoryTree;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
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
     * 单个 CategoryTree → CategoryResponse
     */
    public static CategoryResponse toTreeNode(CategoryTree treeNode) {
        CategoryResponse response = new CategoryResponse();
        response.setId(treeNode.getId());
        response.setName(treeNode.getName());
        response.setLevel(treeNode.getLevel());
        response.setSort(treeNode.getSort());

        // 递归转换子节点
        if (treeNode.getChildren() != null && !treeNode.getChildren().isEmpty()) {
            response.setChildren(toTreeNodeList(treeNode.getChildren()));
        }
        return response;
    }

    /**
     * 树列表转换
     */
    public static List<CategoryResponse> toTreeNodeList(List<CategoryTree> treeList) {
        if (treeList == null || treeList.isEmpty()) {
            return new ArrayList<>();
        }
        return treeList.stream()
                .map(CategoryConverter::toTreeNode)
                .collect(Collectors.toList());
    }
}