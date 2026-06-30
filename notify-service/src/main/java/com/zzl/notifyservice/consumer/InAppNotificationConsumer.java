package com.zzl.notifyservice.consumer;

import com.zzl.commonapi.dto.notifyservicedto.InAppNotificationMessage;
import com.zzl.notifyservice.entity.UserNotification;
import com.zzl.notifyservice.mapper.UserNotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@RocketMQMessageListener(topic = "inapp-notify-topic",consumerGroup = "inapp-notify-group")
public class InAppNotificationConsumer implements RocketMQListener<InAppNotificationMessage> {

    private final UserNotificationMapper userNotificationMapper;

    @Override
    public void onMessage(InAppNotificationMessage msg) {

        log.info("收到站内通知消息: userId={}, title={}", msg.getUserId(), msg.getTitle());

        UserNotification notification = new UserNotification();

        notification.setUserId(msg.getUserId());
        notification.setTitle(msg.getTitle());
        notification.setContent(msg.getContent());
        notification.setType(msg.getType());
        notification.setIsRead(0);
        notification.setRedirectUrl(msg.getRedirectUrl());

        userNotificationMapper.insert(notification);

        log.info("信息已保存至数据库:id={}", notification.getId());

    }
}
