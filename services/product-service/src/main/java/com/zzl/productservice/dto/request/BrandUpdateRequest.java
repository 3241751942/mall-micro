package com.zzl.productservice.dto.request;

import lombok.Data;

@Data
public class BrandUpdateRequest {
    private String name;
    private String logo;
    private Integer sort;
}