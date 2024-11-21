package com.example.hobbyheavy.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationRequest {

    private Long notificationId;  // 읽음 처리할 알림의 ID
    private Boolean isRead;       // 읽음 여부
}
