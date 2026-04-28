package com.zzl.userservice.dto.response;

import com.baomidou.mybatisplus.annotation.*;
import com.zzl.userservice.entity.UserAddress;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


/**
 * 响应地址信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {

    /**
     * 用户地址列表的唯一识别Id
     */
    private Long id;

    /**
     * 关联的用户Id
     */
    private Long userId;

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

    /**
     * 是否为用户的默认地址：0-否，1-是
     */
    private Integer isDefault;   //

    /**
     * 地址记录创建时间
     */
    private LocalDateTime createTime;

    /**
     * 地址记录最近修改时间
     */
    private LocalDateTime updateTime;
}
