package com.zzl.commonapi.feign.userservicefeign;

import com.zzl.commonapi.dto.userservicedto.*;
import com.zzl.commonapi.feign.feignfallback.userservice.UserFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name="user-service",path="/internal/users",contextId = "user", fallbackFactory = UserFeignFallback.class)
public interface UserFeignClient {


    /**
     * 验证用户账号密码
     */
    @PostMapping("/validate")
    UserValidateResponse validate(@RequestBody UserValidateRequest request);

    /**
     * 根据用户ID获取用户基本信息
     */
    @GetMapping("/{userId}")
    UserInfoInternalDTO getUserById(@PathVariable Long userId);

    /**
     * 批量获取用户信息
     */
    @PostMapping("/batch")
    List<UserInfoInternalDTO> batchGetUsers(@RequestBody BatchUserIdsRequest request);

}
