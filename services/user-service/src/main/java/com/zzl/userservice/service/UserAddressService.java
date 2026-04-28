package com.zzl.userservice.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.userservice.entity.UserAddress;

import java.util.List;

public interface UserAddressService extends IService<UserAddress> {
    void setDefaultAddress(Long addressId, Long userId);

    List<UserAddress> getAddressesByUserId(Long userId);

    UserAddress updateAddressById(UserAddress userAddress, Long addressId);

    UserAddress getAddressById(Long addressId);

    void addAddress(UserAddress address);

    void deleteAddressById(Long addressId);

    UserAddress getDefaultAddressByUserId(Long userId);
}
