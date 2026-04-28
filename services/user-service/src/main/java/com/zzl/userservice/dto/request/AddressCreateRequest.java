package com.zzl.userservice.dto.request;


import com.zzl.userservice.entity.UserAddress;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 地址创建
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressCreateRequest {

    /**
     *收货人姓名
     */
    @NotBlank(message = "收货人姓名不能为空")
    private String receiverName;

    /**
     * 收货人手机号码
     */
    @NotBlank(message = "手机号不能为空")
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
    @NotBlank(message = "详细地址不能为空")
    private String detail;

    /**
     * 是否为用户的默认地址：0-否，1-是
     */
    @NotNull(message = "是否默认不能为空")
    private Integer isDefault;  // 0-否 1-是


}
