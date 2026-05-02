package com.zzl.productservice.enums;

import lombok.Getter;

@Getter
public enum ProductServiceBizErrorCode {

    // 业务错误（3xxx）
    PRODUCT_ALREADY_EXISTS(3002, "商品已存在"),
    BRAND_ALREADY_EXISTS(3003, "品牌已存在"),
    CATEGORY_ALREADY_EXISTS(3004, "分类标签已存在"),
    PRODUCT_NOT_FOUND(3005,"该商品不存在"),
    BRAND_NOT_FOUND(3006,"该品牌不存在"),
    CATEGORY_NOT_FOUND(3007,"该分类标签不存在");


    private final Integer code;
    private final String message;

    ProductServiceBizErrorCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}