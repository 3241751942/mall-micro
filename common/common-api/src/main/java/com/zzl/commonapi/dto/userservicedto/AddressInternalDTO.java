package com.zzl.commonapi.dto.userservicedto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对内地址DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressInternalDTO {
    private Long id;
    private Long userId;
    private String receiverName;
    private String phone;
    private String province;
    private String city;
    private String district;
    private String detail;
    private Integer isDefault;
}
