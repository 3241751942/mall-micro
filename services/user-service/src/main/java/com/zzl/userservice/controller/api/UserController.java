package com.zzl.userservice.controller.api;



import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.commoncore.result.Result;
import com.zzl.userservice.converter.UserConverter;
import com.zzl.userservice.dto.request.PasswordUpdateRequest;
import com.zzl.userservice.dto.request.UserRegisterRequest;
import com.zzl.userservice.dto.request.UserUpdateRequest;
import com.zzl.userservice.dto.response.UserInfoResponse;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserController {

    private final UserService userService;


    /**
     * 注册用户
     * @param request 注册用户请求体 UserRegisterRequest
     * @return Result<Void>
     */
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody UserRegisterRequest request) {

        User user = UserConverter.toUser(request);
        userService.register(user);
        return Result.success();
    }

    /**
     * 获取当前用户信息
     * @param userId  用户ID
     * @return Result<UserInfoResponse>
     */
    @GetMapping("/info")
    public Result<UserInfoResponse> getInfo(@RequestHeader("X-User-Id") Long userId) {

        User user= userService.getUserInfo(userId);

        UserInfoResponse response =UserConverter.toUserInfoResponse(user);
        return Result.success(response);
    }

    /**
     * 修改用户信息
     * @param userId 用户ID
     * @param request 修改用户信息请求体 UserUpdateRequest
     * @return   Result<UserInfoResponse> 修改后的用户信息
     */
    @PutMapping("/info")
    public Result<UserInfoResponse> updateInfo(@RequestHeader("X-User-Id") Long userId,
                                   @Valid @RequestBody UserUpdateRequest request) {
        //获取用户信息
        User user=new User();

        UserUpdateRequest.updateUserFromRequest(request,user);

        User user1=userService.updateUserById(user,userId);
        return Result.success(UserConverter.toUserInfoResponse(user1));
    }

    /**
     * 修改密码
     * @param userId 用户ID
     * @param request 修改密码的请求体 PasswordUpdateRequest
     * @return Result<Void>
     */
    @PutMapping("/password")
    public Result<Void> updatePassword(@RequestHeader("X-User-Id") Long userId,
                                       @Valid @RequestBody PasswordUpdateRequest request) {

        userService.updatePassword(userId, request.getNewPassword(), request.getOldPassword());
        return Result.success();
    }

    /**
     * 管理员查询所有用户（分页）
     * @param pageNum 页数量
     * @param pageSize 页大小
     * @param username 需要查找的用户名
     * @param status 账号状态
     * @return Result<Page<UserInfoResponse>>
     */
    @GetMapping("/admin/users")
    public Result<Page<UserInfoResponse>> listUsers(
            @RequestParam(defaultValue = "1") @Min(1) @Max(100) Integer pageNum,
            @RequestParam(defaultValue = "10") @Min(1) Integer pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {

        // 获取实体分页
        Page<User> userPage = userService.listUsers(pageNum, pageSize, username, status);

        // 手动转换，100% 类型安全
        Page<UserInfoResponse> resultPage = new Page<>(
                userPage.getCurrent(),
                userPage.getSize(),
                userPage.getTotal()
        );

        resultPage.setRecords(
                userPage.getRecords().stream()
                        .map(UserConverter::toUserInfoResponse)
                        .toList()
        );

        return Result.success(resultPage);
    }


    /**
     * 管理员启用/禁用用户
     * @param userId 用户id
     * @param status 需要修改成的账号状态
     * @return Result<Void>
     */
    @PutMapping("/admin/users/{userId}/status")
    public Result<Void> changeStatus(@PathVariable Long userId,
                                     @RequestParam @NotNull @Range(max = 1,min = 0) Integer status) {

        userService.changeStatus(userId, status);
        return Result.success();
    }
}
