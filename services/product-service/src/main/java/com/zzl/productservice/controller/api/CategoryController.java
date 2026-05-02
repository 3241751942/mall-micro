package com.zzl.productservice.controller.api;



import com.zzl.commoncore.result.Result;
import com.zzl.productservice.converter.CategoryConverter;
import com.zzl.productservice.dto.response.CategoryResponse;
import com.zzl.productservice.entity.Category;
import com.zzl.productservice.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类
 */
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类树
     * @return 顶级分类列表（每个分类包含其子分类）
     */
    @GetMapping("/tree")
    public Result<List<CategoryResponse>> getTree() {
        List<Category> tree = categoryService.getCategoryTree();

        return Result.success(CategoryConverter.toTreeNodeList(tree));
    }
}
