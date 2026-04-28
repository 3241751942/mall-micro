package com.zzl.commonapi.fegin.feignfallback.userservice;

import com.zzl.commonapi.dto.userservicedto.*;
import com.zzl.commonapi.fegin.userservicefeign.UserFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.List;

public class UserFeignFallback implements FallbackFactory<UserFeignClient> {
    @Override
    public UserFeignClient create(Throwable cause) {
        return new UserFeignClient() {

            @Override
            public UserValidateResponse validate(UserValidateRequest request){
                return null;
            }
            @Override

            public UserInfoInternalDTO getUserById(Long userId){
                return null;
            }
            @Override
            public List<UserInfoInternalDTO> batchGetUsers(BatchUserIdsRequest request){
                return List.of();
            }
        };
    }
}
