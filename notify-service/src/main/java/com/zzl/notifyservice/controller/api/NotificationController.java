package com.zzl.notifyservice.controller.api;
import com.alibaba.nacos.api.model.v2.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zzl.notifyservice.entity.UserNotification;
import com.zzl.notifyservice.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户通知接口
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final UserNotificationService notificationService;

    /**
     * 获取未读消息数量（小红点）
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long count = notificationService.getUnreadCount(userId);
        return Result.success(count);
    }

    /**
     * 获取未读消息列表
     */
    @GetMapping("/unread")
    public Result<List<UserNotification>> getUnreadList(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null) {
            return Result.success(List.of());
        }
        List<UserNotification> list = notificationService.getUnreadList(userId);
        return Result.success(list);
    }

    /**
     * 分页查询消息列表（全部消息）
     */
    @GetMapping("/page")
    public Result<Page<UserNotification>> getPage(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "20") Integer pageSize) {
        if (userId == null) {
            return Result.success(new Page<>());
        }
        Page<UserNotification> page = notificationService.getPage(userId, pageNum, pageSize);
        return Result.success(page);
    }

    /**
     * 标记单个消息为已读
     */
    @PutMapping("/{id}/read")
    public Result<Void> markAsRead(
            @RequestHeader("X-User-Id") Long userId,
            @PathVariable("id") Long notificationId) {
        notificationService.markAsRead(userId, notificationId);
        return Result.success();
    }

    /**
     * 全部已读
     */
    @PutMapping("/read-all")
    public Result<Void> readAll(@RequestHeader("X-User-Id") Long userId) {
        notificationService.readAll(userId);
        return Result.success();
    }
}