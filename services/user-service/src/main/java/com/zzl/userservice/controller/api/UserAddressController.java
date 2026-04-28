package com.zzl.userservice.controller.api;

import com.zzl.commoncore.result.Result;
import com.zzl.userservice.converter.UserConverter;
import com.zzl.userservice.dto.request.AddressCreateRequest;
import com.zzl.userservice.dto.request.AddressUpdateRequest;
import com.zzl.userservice.dto.response.AddressResponse;
import com.zzl.userservice.entity.UserAddress;
import com.zzl.userservice.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
@Validated
public class UserAddressController {

    private final UserAddressService userAddressService;

    /**
     * 获取当前用户的地址列表
     * @param userId 用户Id
     * @return 当前用户的地址列表 Result<List<AddressResponse>>
     */
    @GetMapping
    public Result<List<AddressResponse>> listAddresses(@RequestHeader("X-User-Id") Long userId) {

        List<UserAddress> addresses=userAddressService.getAddressesByUserId(userId);

        List<AddressResponse> responses = addresses.stream()
                .map(UserConverter::toAddressResponse)
                .toList();
        return Result.success(responses);
    }

    /**
     * 新增地址
     * @param userId 用户Id
     * @param request 新增的地址 请求体
     * @return 新增的地址 Result<AddressResponse>
     */
    @PostMapping
    public Result<AddressResponse> addAddress(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody AddressCreateRequest request) {

        UserAddress address=UserConverter.toUserAddress(request,userId);

        userAddressService.addAddress(address);

        //打上标签@TableId(type = IdType.AUTO)之后会自动获取Id，不需要重新去数据库获取userAddress对象
        AddressResponse addressResponse=UserConverter.toAddressResponse(address);

        return Result.success(addressResponse);
    }


    /**
     * 修改地址
     * @param userId 用户Id
     * @param addressId 地址Id
     * @param request 修改后的地址 请求体
     * @return Result<UserAddress> 修改后的地址
     */
    @PutMapping("/{addressId}")
    public Result<AddressResponse> updateAddress(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long addressId,
                                      @Valid @RequestBody AddressUpdateRequest request) {
        UserAddress userAddress=new UserAddress();
        UserConverter.updateAddressFromRequest(request,userAddress);

        UserAddress address=userAddressService.updateAddressById(userAddress,addressId);

        return Result.success(UserConverter.toAddressResponse(address));
    }

    /**
     * 删除地址
     * @param userId 用户Id
     * @param addressId 地址Id
     * @return Result<Void>
     */
    @DeleteMapping("/{addressId}")
    public Result<Void> deleteAddress(@RequestHeader("X-User-Id") Long userId,
                                      @PathVariable Long addressId) {
        userAddressService.deleteAddressById(addressId);
        return Result.success();
    }

    /**
     * 设置默认地址
     * @param userId 用户Id
     * @param addressId 默认的地址的Id
     * @return Result<Void>
     */
    @PutMapping("/default/{addressId}")
    public Result<Void> setDefaultAddress(@RequestHeader("X-User-Id") Long userId,
                                          @PathVariable Long addressId) {
        userAddressService.setDefaultAddress(addressId, userId);
        return Result.success();
    }
}