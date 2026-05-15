package com.zzl.commonapi.feign.userservicefeign;

import com.zzl.commonapi.dto.userservicedto.AddressInternalDTO;
import com.zzl.commonapi.feign.feignfallback.userservice.AddressFeignFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="user-service",path = "/internal/UserAddress",contextId = "address",fallbackFactory = AddressFeignFallback.class)
public interface AddressFeignClient {

    /**
     * 获取用户的默认收货地址
     * @param userId 用户Id
     * @return  默认收货地址
     */
    @GetMapping("/{userId}/default-address")
    AddressInternalDTO getDefaultAddress(@PathVariable("userId") Long userId);

    /**
     * 获取用户的所有地址列表
     * @param userId 用户Id
     * @return 所有地址列表
     */
    @GetMapping("/{userId}/addresses")
    List<AddressInternalDTO> getUserAddresses(@PathVariable("userId") Long userId);

    /**
     * 根据地址ID获取地址详情
     * @param addressId 地址ID
     * @return 地址详情
     */
    @GetMapping("/addresses/{addressId}")
    AddressInternalDTO getAddressById(@PathVariable("addressId") Long addressId);
}
