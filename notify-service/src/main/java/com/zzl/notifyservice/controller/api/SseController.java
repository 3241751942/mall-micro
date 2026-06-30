package com.zzl.notifyservice.controller.api;

import com.zzl.notifyservice.mapper.UserNotificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/css")
@RequiredArgsConstructor
@Slf4j
public class SseController {

    private final UserNotificationMapper notificationMapper;

    // 存储每个用户的 SSE 连接
    private static final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@RequestHeader("X-User-Id") Long userId) {
        log.info("用户 {} 建立 SSE 连接", userId);

        // 设置超时时间 5 分钟
        SseEmitter emitter = new SseEmitter(5 * 60 * 1000L);

        // 保存连接
        emitters.put(userId, emitter);

        // 断开连接时移除
        emitter.onCompletion(() -> emitters.remove(userId));
        emitter.onTimeout(() -> emitters.remove(userId));

        return emitter;
    }

    /**
     * 推送通知到指定用户（由消费者调用）
     */
    public static void pushToUser(Long userId, String title, String content) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null) {
            log.info("用户 {} 不在线，不推送", userId);
            return;
        }

        try {
            SseEmitter.SseEventBuilder event = SseEmitter.event()
                    .data(Map.of("title", title, "content", content, "time", System.currentTimeMillis()))
                    .name("notification");
            emitter.send(event);
            log.info("已推送给用户 {}: {}", userId, title);
        } catch (IOException e) {
            log.warn("推送失败，用户 {} 连接可能已断开", userId);
            emitters.remove(userId);
        }
    }
}