package com.zzl.productservice.dto.response;

import lombok.Data;

@Data
public class BrandResponse {
    private Long id;
    private String name;
    private String logo;
    private Integer sort;
}
