package com.zzl.userservice.dto.request;


import com.zzl.userservice.entity.User;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 修改用户信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    /**
     *用户昵称（网名）
     */
    private String nickname;

    /**
     * 头像照片url
     */
    private String avatar;   // 头像 URL


    /**
     * 用户手机号码
     */
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "手机号格式不正确")
    private String phone;


    /**
     * 用户邮箱
     */
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "邮箱格式不正确")
    private String email;


    /**
     * 用户更新请求 -> 更新用户实体
     * @param request 更新请求的DTO
     * @param user 需要更新的实体User
     */
    public static void updateUserFromRequest(UserUpdateRequest request, User user) {
        if (request == null) return;
        if (request.nickname != null) user.setNickname(request.nickname);
        if (request.avatar != null) user.setAvatar(request.avatar);
        if (request.phone != null) user.setPhone(request.phone);
        if (request.email != null) user.setEmail(request.email);
    }


}
