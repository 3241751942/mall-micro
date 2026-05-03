package com.zzl.commonapi.fegin.stockservicefeign;


import com.zzl.commonapi.dto.stockservicedto.StockDTO;
import com.zzl.commonapi.dto.stockservicedto.StockLockRequest;
import com.zzl.commonapi.dto.stockservicedto.StockLockResult;
import com.zzl.commonapi.fegin.feignfallback.stockservice.StockFeignFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "stock-service", path = "/internal/stocks", fallbackFactory = StockFeignFallbackFactory.class)
public interface StockFeignClient {

    /**
     * 锁定库存
     * @param request 锁定请求，包含商品ID、数量、订单号
     * @return 锁定结果，包含是否成功、提示信息、剩余可用库存等
     */
    @PostMapping("/lock")
    StockLockResult lock(@RequestBody StockLockRequest request);

    /**
     * 确认扣减库存
     * @param request 扣减请求，包含商品ID、数量、订单号
     * @return true-扣减成功；false-扣减失败
     */
    @PostMapping("/confirm")
    boolean confirm(@RequestBody StockLockRequest request);

    /**
     * 解锁库存
     * @param request 解锁请求，包含商品ID、数量、订单号
     * @return true-解锁成功；false-解锁失败
     */
    @PostMapping("/unlock")
    boolean unlock(@RequestBody StockLockRequest request);

    /**
     * 批量查询库存信息
     * @param productIds 商品ID列表
     * @return 库存信息列表
     */
    @PostMapping("/batch")
    List<StockDTO> batchQuery(@RequestBody List<Long> productIds);
}