package com.zzl.notifyservice.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zzl.notifyservice.entity.UserNotification;
import com.zzl.notifyservice.mapper.UserNotificationMapper;
import com.zzl.notifyservice.service.UserNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserNotificationServiceImpl extends ServiceImpl<UserNotificationMapper, UserNotification> implements UserNotificationService {



    @Transactional(readOnly = true)
    @Override
    public Long getUnreadCount(Long userId) {
        Long count=lambdaQuery().eq(UserNotification::getUserId, userId).count();
        if(count==null){
            throw new RuntimeException("获取未读消息失败，用户id："+userId);
        }
        return count;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserNotification> getUnreadList(Long userId) {
        return lambdaQuery().eq(UserNotification::getUserId, userId).list();
    }

    @Transactional(readOnly = true)
    @Override
    public Page<UserNotification> getPage(Long userId, Integer pageNum, Integer pageSize) {
        Page<UserNotification> page = new Page<>(pageNum, pageSize);
        return lambdaQuery().eq(UserNotification::getUserId, userId).page(page);
    }

    @Transactional
    @Override
    public void markAsRead(Long userId, Long notificationId) {

        UserNotification notification=lambdaQuery().eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getId, notificationId)
                .one();

        if(notification == null){
            throw new RuntimeException("该消息记录不存在");
        }

        boolean result=lambdaUpdate().eq(UserNotification::getUserId, userId)
                .eq(UserNotification::getId, notificationId)
                .set(UserNotification::getIsRead, 1)
                .update();
        if(!result){
            throw new RuntimeException("将单条消息修改为已读失败");
        }
    }

    @Override
    public void readAll(Long userId) {
        lambdaUpdate().eq(UserNotification::getUserId, userId)
                .set(UserNotification::getIsRead, 1)
                .update();
    }
}
