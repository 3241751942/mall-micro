package com.zzl.commonapi.feign.logservicefeign;


import com.zzl.commonapi.dto.logservicedto.LogRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "log-service", path = "/api/logs")
public interface LogFeignClient {
    @PostMapping
    void addLog(@RequestBody LogRequest request);
}