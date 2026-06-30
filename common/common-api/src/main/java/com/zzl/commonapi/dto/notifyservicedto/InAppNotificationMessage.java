package com.zzl.commonapi.dto.notifyservicedto;
import lombok.Data;

@Data
public class InAppNotificationMessage {
    private Long userId;
    private String title;
    private String content;
    private String type;          // ORDER / PROMOTION / SYSTEM
    private String redirectUrl;
}