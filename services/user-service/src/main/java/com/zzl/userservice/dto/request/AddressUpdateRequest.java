package com.zzl.userservice.dto.request;


import com.zzl.userservice.entity.UserAddress;
import com.zzl.userservice.service.serviceimpl.UserAddressServiceImpl;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 *  地址更新
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressUpdateRequest {

    /**
     *收货人姓名
     */
    private String receiverName;
    /**
     * 收货人手机号码
     */
    private String phone;

    /**
     * 省/直辖市
     */
    private String province;

    /**
     * 市/州
     */
    private String city;

    /**
     * 区/县/旗
     */
    private String district;

    /**
     * 详细地址（街道、小区、门牌号等）
     */
    private String detail;

}