package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.NotificationResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;

    // 특정 사용자의 알림 목록 조회
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is retrieving their notifications", userId);
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 특정 알림 읽음 처리
    @PutMapping("/{notificationId}/mark-as-read")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            notificationService.markAsRead(notificationId);
            log.info("Notification with ID {} marked as read", notificationId);
            return ResponseEntity.ok("알림이 읽음 처리되었습니다.");
        } catch (CustomException e) {
            log.error("Error occurred while marking notification as read: {}", e.getMessage());
            if (e.getExceptionCode() == ExceptionCode.NOTIFICATION_NOT_FOUND) {
                return ResponseEntity.status(404).body("알림을 찾을 수 없습니다.");
            }
            return ResponseEntity.status(500).body("예기치 못한 오류가 발생했습니다.");
        }
    }

}
