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

/**
 * 알림(Notification) 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/notifications") // 알림 API 기본 경로
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
@Slf4j // 로깅을 위한 Lombok 어노테이션
public class NotificationController {

    private final NotificationService notificationService; // 알림 관련 비즈니스 로직 처리 서비스

    /**
     * 특정 사용자의 알림 목록 조회
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자의 알림 목록
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_USER')") // ROLE_USER 권한을 가진 사용자만 접근 가능
    public ResponseEntity<List<NotificationResponse>> getUserNotifications(Authentication authentication) {
        // 인증 객체에서 사용자 ID 추출
        String userId = authentication.getName();
        log.info("User {} is retrieving their notifications", userId); // 알림 조회 로그 기록

        // 사용자 ID를 기반으로 알림 목록 조회
        List<NotificationResponse> notifications = notificationService.getUserNotifications(userId);

        // 알림 목록 반환
        return ResponseEntity.ok(notifications);
    }

    /**
     * 특정 알림 읽음 처리
     *
     * @param notificationId 읽음 처리할 알림 ID
     * @return 처리 결과 메시지
     */
    @PutMapping("/{notificationId}/read")
    @PreAuthorize("hasRole('ROLE_USER')") // ROLE_USER 권한을 가진 사용자만 접근 가능
    public ResponseEntity<String> markNotificationAsRead(@PathVariable Long notificationId) {
        try {
            // 알림 읽음 처리 서비스 호출
            notificationService.markAsRead(notificationId);
            log.info("Notification with ID {} marked as read", notificationId); // 처리 성공 로그 기록
            return ResponseEntity.ok("알림이 읽음 처리되었습니다.");
        } catch (CustomException e) {
            // 예외 발생 시 에러 로그 기록 및 응답 처리
            log.error("Error occurred while marking notification as read: {}", e.getMessage());

            // 알림이 존재하지 않는 경우 404 상태 코드 반환
            if (e.getExceptionCode() == ExceptionCode.NOTIFICATION_NOT_FOUND) {
                return ResponseEntity.status(404).body("알림을 찾을 수 없습니다.");
            }

            // 기타 예기치 못한 오류 발생 시 500 상태 코드 반환
            return ResponseEntity.status(500).body("예기치 못한 오류가 발생했습니다.");
        }
    }
}
