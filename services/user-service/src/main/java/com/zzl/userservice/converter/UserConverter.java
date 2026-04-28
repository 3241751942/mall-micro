package com.zzl.userservice.converter;

import com.zzl.commonapi.dto.userservicedto.AddressInternalDTO;
import com.zzl.commonapi.dto.userservicedto.UserInfoInternalDTO;
import com.zzl.commonapi.dto.userservicedto.UserValidateResponse;
import com.zzl.userservice.dto.request.AddressCreateRequest;
import com.zzl.userservice.dto.request.AddressUpdateRequest;
import com.zzl.userservice.dto.request.UserRegisterRequest;
import com.zzl.userservice.dto.request.UserUpdateRequest;
import com.zzl.userservice.dto.response.AddressResponse;
import com.zzl.userservice.dto.response.UserInfoResponse;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.entity.UserAddress;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户与地址的实体与DTO转换器
 */
public class UserConverter {


    /**
     * 注册请求 -> 用户实体
     */
    public static User toUser(UserRegisterRequest request) {
        if (request == null) return null;
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());   // 注意：密码未加密，调用方需后续加密
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setNickname(request.getNickname() != null ? request.getNickname() : request.getUsername());
        user.setStatus(1);   // 默认启用
        return user;
    }

    /**
     * 用户更新请求 -> 更新用户实体（只复制非空字段，用于部分更新）
     */
    public static void updateUserFromRequest(UserUpdateRequest request, User user) {
        if (request == null) return;
        if (request.getNickname() != null) user.setNickname(request.getNickname());
        if (request.getAvatar() != null) user.setAvatar(request.getAvatar());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        if (request.getEmail() != null) user.setEmail(request.getEmail());
    }

    /**
     * 用户实体 -> 对外响应（脱敏）
     */
    public static UserInfoResponse toUserInfoResponse(User user) {
        if (user == null) return null;
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        if (user.getLastLoginTime() != null) {
            response.setLastLoginTime(user.getLastLoginTime());
        }
        // 手机号脱敏
        if (response.getPhone() != null && response.getPhone().length() == 11) {
            response.setPhone(response.getPhone().substring(0, 3) + "****" + response.getPhone().substring(7));
        }
        // 邮箱脱敏
        if (response.getEmail() != null && response.getEmail().contains("@")) {
            String[] parts = response.getEmail().split("@");
            String local = parts[0];
            if (local.length() > 2) {
                local = local.substring(0, 2) + "***";
            }
            response.setEmail(local + "@" + parts[1]);
        }
        return response;
    }

    /**
     * 用户实体 -> 对内验证响应（不脱敏，返回完整信息）
     */
    public static UserValidateResponse toUserValidateResponse(User user) {
        if (user == null) return null;
        UserValidateResponse response = new UserValidateResponse();
        BeanUtils.copyProperties(user, response);
        return response;
    }

    /**
     * 用户实体 -> 对内详情DTO
     */
    public static UserInfoInternalDTO toUserInfoInternalDTO(User user) {
        if (user == null) return null;
        UserInfoInternalDTO dto = new UserInfoInternalDTO();
        BeanUtils.copyProperties(user, dto);
        if (user.getLastLoginTime() != null) {
            dto.setLastLoginTime(user.getLastLoginTime());
        }
        return dto;
    }

    /**
     * 批量用户实体 -> 批量对内DTO
     */
    public static List<UserInfoInternalDTO> toUserInfoInternalDTOList(List<User> users) {
        if (users == null) return List.of();
        return users.stream()
                .map(UserConverter::toUserInfoInternalDTO)
                .collect(Collectors.toList());
    }

    // ========== 地址转换 ==========

    /**
     * 地址创建请求 -> 地址实体
     */
    public static UserAddress toUserAddress(AddressCreateRequest request, Long userId) {
        if (request == null) return null;
        UserAddress address = new UserAddress();
        address.setUserId(userId);
        address.setReceiverName(request.getReceiverName());
        address.setPhone(request.getPhone());
        address.setProvince(request.getProvince());
        address.setCity(request.getCity());
        address.setDistrict(request.getDistrict());
        address.setDetail(request.getDetail());
        address.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : 0);
        return address;
    }

    /**
     * 地址更新请求 -> 更新地址实体（部分更新，非空字段覆盖）
     */
    public static void updateAddressFromRequest(AddressUpdateRequest request, UserAddress address) {
        if (request == null) return;
        if (request.getReceiverName() != null) address.setReceiverName(request.getReceiverName());
        if (request.getPhone() != null) address.setPhone(request.getPhone());
        if (request.getProvince() != null) address.setProvince(request.getProvince());
        if (request.getCity() != null) address.setCity(request.getCity());
        if (request.getDistrict() != null) address.setDistrict(request.getDistrict());
        if (request.getDetail() != null) address.setDetail(request.getDetail());
    }

    /**
     * 地址实体 -> 对外响应
     */
    public static AddressResponse toAddressResponse(UserAddress address) {
        if (address == null) return null;
        AddressResponse response = new AddressResponse();
        BeanUtils.copyProperties(address, response);
        if (address.getCreateTime() != null) {
            response.setCreateTime(address.getCreateTime());
        }
        if (address.getUpdateTime() != null) {
            response.setUpdateTime(address.getUpdateTime());
        }
        return response;
    }

    /**
     * 地址实体 -> 对内地址DTO
     */
    public static AddressInternalDTO toAddressInternalDTO(UserAddress address) {
        if (address == null) return null;
        AddressInternalDTO dto = new AddressInternalDTO();
        BeanUtils.copyProperties(address, dto);
        return dto;
    }

    /**
     * 批量地址实体 -> 批量对内DTO
     */
    public static List<AddressInternalDTO> toAddressInternalDTOList(List<UserAddress> addresses) {
        if (addresses == null) return List.of();
        return addresses.stream()
                .map(UserConverter::toAddressInternalDTO)
                .collect(Collectors.toList());
    }
}