package com.zzl.notifyservice.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zzl.notifyservice.entity.UserNotification;

import java.util.List;

public interface UserNotificationService extends IService<UserNotification> {

    /**
     * 获取未读消息数量
     * @param userId 用户id
     * @return 未读消息数量 int
     */
    Long getUnreadCount(Long userId);

    /**
     * 获取未读消息列表
     * @param userId 用户id
     * @return 未读消息列表
     */
    List<UserNotification> getUnreadList(Long userId);

    /**
     * 分页查询消息列表（全部消息）
     * @param userId 用户id
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 消息列表
     */
    Page<UserNotification> getPage(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 标记单个消息为已读
     * @param userId 用户id
     * @param notificationId 消息id
     */
    void markAsRead(Long userId, Long notificationId);

    /**
     * 全部已读
     * @param userId 用户id
     */
    void readAll(Long userId);
}
