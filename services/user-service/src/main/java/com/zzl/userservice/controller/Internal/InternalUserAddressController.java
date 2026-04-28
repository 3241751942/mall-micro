package com.zzl.userservice.controller.Internal;


import com.zzl.commonapi.dto.userservicedto.AddressInternalDTO;
import com.zzl.userservice.converter.UserConverter;
import com.zzl.userservice.entity.UserAddress;
import com.zzl.userservice.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("internal/UserAddress")
@RequiredArgsConstructor
public class InternalUserAddressController {


    private final UserAddressService userAddressService;
    /**
     * 获取用户的默认收货地址
     * @param userId 用户Id
     * @return  默认收货地址
     */
    @GetMapping("/{userId}/default-address")
    public AddressInternalDTO getDefaultAddress(@PathVariable Long userId) {
        UserAddress address = userAddressService.getDefaultAddressByUserId(userId);
        return UserConverter.toAddressInternalDTO(address);
    }

    /**
     * 获取用户的所有地址列表
     * @param userId 用户Id
     * @return 所有地址列表
     */
    @GetMapping("/{userId}/addresses")
    public List<AddressInternalDTO> getUserAddresses(@PathVariable Long userId) {
        List<UserAddress> addresses = userAddressService.getAddressesByUserId(userId);
        return UserConverter.toAddressInternalDTOList(addresses);
    }

    /**
     * 根据地址ID获取地址详情
     * @param addressId 地址ID
     * @return 地址详情
     */
    @GetMapping("/addresses/{addressId}")
    public AddressInternalDTO getAddressById(@PathVariable Long addressId) {
        UserAddress address = userAddressService.getAddressById(addressId);
        return UserConverter.toAddressInternalDTO(address);
    }

}
