package com.zzl.userservice.controller.Internal;



import com.zzl.commonapi.dto.userservicedto.BatchUserIdsRequest;
import com.zzl.commonapi.dto.userservicedto.UserInfoInternalDTO;
import com.zzl.commonapi.dto.userservicedto.UserValidateRequest;
import com.zzl.commonapi.dto.userservicedto.UserValidateResponse;
import com.zzl.userservice.converter.UserConverter;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;


    /**
     * 验证用户账号密码
     */
    @PostMapping("/validate")
    public UserValidateResponse validate(@Valid @RequestBody UserValidateRequest request) {
        User user = userService.validateUser(request.getAccount(), request.getPassword());
        return UserConverter.toUserValidateResponse(user);
    }

    /**
     * 根据用户ID获取用户基本信息
     */
    @GetMapping("/{userId}")
    public UserInfoInternalDTO getUserById(@PathVariable Long userId) {
        User user = userService.getById(userId);
        return UserConverter.toUserInfoInternalDTO(user);
    }

    /**
     * 批量获取用户信息
     */
    @PostMapping("/batch")
    public List<UserInfoInternalDTO> batchGetUsers(@RequestBody BatchUserIdsRequest request) {
        List<User> users = userService.listByIds(request.getUserIds());
        return UserConverter.toUserInfoInternalDTOList(users);
    }


}
