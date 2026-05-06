package com.zzl.commonapi.feign.productservicefeign;

import com.zzl.commonapi.dto.productservicedto.ProductInternalDTO;
import com.zzl.commonapi.feign.feignfallback.productservice.ProductFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


@FeignClient(name = "product-service", path = "/internal/products", fallbackFactory = ProductFeignFallbackFactory.class)
public interface ProductFeignClient {

    /**
     * 批量获取商品基本信息
     * @param productIds 商品ID列表
     * @return 商品基本信息列表，不含库存字段
     */
    @PostMapping("/batch")
    List<ProductInternalDTO> batchGetProducts(@RequestBody List<Long> productIds);
}