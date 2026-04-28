package com.zzl.userservice.service.serviceimpl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.userservice.entity.User;
import com.zzl.userservice.enums.UserServiceBizErrorCode;
import com.zzl.userservice.exception.UserNotFoundException;
import com.zzl.userservice.mapper.UserMapper;
import com.zzl.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;


    /**
     * 获取当前用户信息
     * @param userId 用户Id
     * @return 用户 User
     */
    @Override
    public User getUserInfo(Long userId) {
        User user=getById(userId);
        if (user==null){
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND);
        }
        return user;
    }

    /**
     * 修改账号密码
     * @param userId 用户Id
     * @param newPassword 新密码
     */
    @Override
    @Transactional
    public void updatePassword(Long userId, String newPassword, String oldPassword) {
        User user = getById(userId);
        if (user == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND, "原密码错误");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        boolean updated = updateById(user);
        if (!updated) {
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND, "密码修改失败");
        }
    }

    /**
     * 修改账号状态
     * @param userId 用户Id
     * @param status 想修改成的状态
     */
    @Override
    public void changeStatus(Long userId, Integer status) {
        boolean updated=lambdaUpdate().eq(User::getId,userId)
                            .set(User::getStatus,status)
                            .update();
        if (!updated){
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"账号状态修改失败");
        }
    }

    /**
     * 管理员查询所有用户（分页）
     * @param pageNum 页数量
     * @param pageSize 页大小
     * @param username 需要查找的用户名
     * @param status 账号状态
     * @return Page<User>
     */
    @Override
    public Page<User> listUsers(Integer pageNum, Integer pageSize, String username, Integer status) {
        Page<User> page = new Page<>(pageNum, pageSize);

        lambdaQuery()
                .like(StringUtils.hasText(username), User::getUsername, username)
                .eq(status != null, User::getStatus, status)
                .page(page);

        return page;
    }

    /**
     * 添加用户
     * @param user 添加的用户的实体
     */
    @Override
    public void register(User user) {
        Long count=lambdaQuery()
                .eq(User::getUsername,user.getUsername())
                .count();
        if (count>0){
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"用户账号已存在");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        boolean saved=save(user);
        if (!saved){
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"用户注册失败");
        }
    }


    /**
     * 用户信息修改
     * @param request 想修改成的信息实体
     */
    @Override
    @Transactional
    public User updateUserById(User request,Long userId) {
        User user=getById(userId);
        if (user==null){
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"用户Id错误");
        }
        if (request != null){
            UserUpdateIsNull(user, request.getNickname(), request.getAvatar(), request.getPhone(), request.getEmail(), request);
            boolean updated=updateById(user);
            if (!updated){
                throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"用户信息修改失败");
            }
        }
        return getById(userId);
    }

    /**
     *
     * @param account 账号
     * @param password 密码
     * @return User账号信息
     */

    @Override
    public User validateUser(String account, String password) {
        User user = lambdaQuery()
                .eq(User::getUsername, account)
                .one();
        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new UserNotFoundException(UserServiceBizErrorCode.USER_NOT_FOUND,"账号或密码错误");
        }
        return user;
    }

    /**
     * 中间转换，等下尝试用BeanUtils.copyProperties替换
     */
    public static void UserUpdateIsNull(User user, String nickname, String avatar, String phone, String email, User request) {
        if (nickname != null) user.setNickname(nickname);
        if (avatar != null) user.setAvatar(avatar);
        if (phone != null) user.setPhone(phone);
        if (email != null) user.setEmail(email);
    }
}
