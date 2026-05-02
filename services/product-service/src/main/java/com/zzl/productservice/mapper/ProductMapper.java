package com.zzl.productservice.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.productservice.entity.Product;
import com.zzl.productservice.entity.ProductResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;

@Mapper
public interface ProductMapper extends BaseMapper<Product>{

    /**
     * 查询商品详情（关联品牌和分类名称）
     * @param productId 商品ID
     * @return ProductResponseDTO
     */
    ProductResponseDTO selectProductDetail(@Param("productId") Long productId);

    /**
     * 分页查询商品（关联品牌和分类名称）
     * @param page         MyBatis-Plus 分页对象（我也不知道为啥要必须第一个参数）
     * @param categoryId   分类ID
     * @param brandId      品牌ID
     * @param minPrice     最低价格
     * @param maxPrice     最高价格
     * @param keyword      关键字（模糊匹配商品名称）
     * @param sortBy       排序字段：price, sales, createTime
     * @param order        排序方向：asc, desc
     * @return 分页结果，记录类型为 ProductResponseDTO
     */
    Page<ProductResponseDTO> selectProductPage(Page<ProductResponseDTO> page,
                                               @Param("categoryId") Long categoryId,
                                               @Param("brandId") Long brandId,
                                               @Param("minPrice") BigDecimal minPrice,
                                               @Param("maxPrice") BigDecimal maxPrice,
                                               @Param("keyword") String keyword,
                                               @Param("sortBy") String sortBy,
                                               @Param("order") String order);
}
