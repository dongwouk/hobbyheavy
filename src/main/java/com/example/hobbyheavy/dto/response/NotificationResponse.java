package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.type.NotificationType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {

    private Long notificationId;          // 알림 ID
    private String userId;                // 사용자 ID
    private String message;               // 알림 메시지
    private NotificationType type;        // 알림 유형 (예: 스케줄 생성, 확정 등)
    private Boolean isRead;               // 알림 읽음 여부
    private LocalDateTime createdDate;    // 알림 생성일자
}