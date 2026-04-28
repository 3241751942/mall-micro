package com.zzl.userservice.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_address")
public class UserAddress {

    /**
     * 用户地址列表的唯一识别Id
     */
    @TableId(type = IdType.AUTO)
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
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 地址记录最近修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 逻辑删除标记: 0-未删除，1-已删除 (删除地址时仅仅修改标记)：查询时过滤0
     */
    @TableLogic
    private Integer deleted;
}