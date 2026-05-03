package com.zzl.commonapi.fegin.feignfallback.stockservice;

import com.zzl.commonapi.dto.stockservicedto.StockDTO;
import com.zzl.commonapi.dto.stockservicedto.StockLockRequest;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.commonapi.fegin.stockservicefeign.StockFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class StockFeignFallbackFactory implements FallbackFactory<StockFeignClient> {
    @Override
    public StockFeignClient create(Throwable cause) {
        return new StockFeignClient() {
            @Override
            public StockLockResult lock(StockLockRequest request) {
                StockLockResult result = new StockLockResult();
                result.setSuccess(false);
                result.setMessage("库存服务不可用");
                return result;
            }

            @Override
            public boolean confirm(StockLockRequest request) {
                return false;
            }

            @Override
            public boolean unlock(StockLockRequest request) {
                return false;
            }

            @Override
            public List<StockDTO> batchQuery(List<Long> productIds) {
                return List.of();
            }
        };
    }
}
