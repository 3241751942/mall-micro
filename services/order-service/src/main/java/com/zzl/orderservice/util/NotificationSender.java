package com.zzl.orderservice.util;

import com.zzl.commonapi.dto.notifyservicedto.InAppNotificationMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationSender {

    private final RocketMQTemplate rocketMQTemplate;

    public void sendNotification(Long userId, String title, String content, String redirectUrl){

        InAppNotificationMessage msg = new InAppNotificationMessage();
        msg.setUserId(userId);
        msg.setTitle(title);
        msg.setContent(content);
        msg.setRedirectUrl(redirectUrl);
        msg.setType("ORDER");

        rocketMQTemplate.convertAndSend("inapp-notify-topic",msg);
        log.info("通知消息已发送: userId={}, title={}", userId, title);
    }
}
