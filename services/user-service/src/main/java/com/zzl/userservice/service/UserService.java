package com.zzl.userservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.userservice.entity.User;

public interface UserService extends IService<User> {

    User getUserInfo(Long userId);

    void updatePassword(Long userId,String newPassword,String oldPassword);

    void changeStatus(Long userId, Integer status);

    Page<User> listUsers(Integer pageNum, Integer pageSize, String username, Integer status);

    void register(User user);

    User updateUserById(User user,Long userId);

    User validateUser(String account, String password);
}
