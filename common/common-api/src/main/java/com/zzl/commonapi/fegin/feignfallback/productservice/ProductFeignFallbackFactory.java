package com.zzl.commonapi.fegin.feignfallback.productservice;

import com.zzl.commonapi.dto.productservicedto.ProductInternalDTO;
import com.zzl.commonapi.fegin.productservicefeign.ProductFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class ProductFeignFallbackFactory implements FallbackFactory<ProductFeignClient> {

    @Override
    public ProductFeignClient create(Throwable cause) {
        log.error("商品服务调用失败: {}", cause.getMessage(), cause);
        return new ProductFeignClient() {

            @Override
            public List<ProductInternalDTO> batchGetProducts(List<Long> productIds) {
                log.warn("降级：返回空商品列表，原请求 productIds = {}", productIds);
                return Collections.emptyList();
            }
        };
    }
}
