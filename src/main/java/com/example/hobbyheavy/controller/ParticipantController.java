package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ParticipantRoleRequest;
import com.example.hobbyheavy.dto.request.ParticipantStatusRequest;
import com.example.hobbyheavy.dto.response.ParticipantWaitResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Participant(참여자) 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/participants") // Participant API 기본 경로
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class ParticipantController {

    private final ParticipantService participantService; // 참여자 관련 비즈니스 로직 처리

    /**
     * 인증된 사용자 ID를 가져오는 메서드
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자 ID
     */
    private String getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER); // 인증되지 않은 사용자 예외
        }
        return authentication.getName(); // 사용자 ID 반환
    }

    /**
     * 참여 요청 (대기 상태로 추가)
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 생성 성공 메시지
     */
    @PostMapping("/{meetupId}")
    public ResponseEntity<String> waitParticipant(
            @PathVariable Long meetupId, Authentication authentication) {
        participantService.joinParticipant(meetupId, getUserId(authentication));
        return ResponseEntity.status(201).body("Waiting Participant Successfully.");
    }

    /**
     * 참여 대기 취소
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 취소 성공 메시지
     */
    @PutMapping("/{meetupId}/waiting")
    public ResponseEntity<String> waitCancelParticipant(
            @PathVariable Long meetupId, Authentication authentication) {
        participantService.cancelWaiting(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Cancel Waiting Participant Successfully.");
    }

    /**
     * 특정 Meetup의 참여 대기자 목록 조회
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 참여 대기자 목록
     */
    @GetMapping("/{meetupId}/waiting")
    public ResponseEntity<List<ParticipantWaitResponse>> waitParticipants(
            @PathVariable Long meetupId, Authentication authentication) {
        return ResponseEntity.ok(
                participantService.getWaitParticipant(meetupId, getUserId(authentication)));
    }

    /**
     * Meetup 알림 설정/해제 토글
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 토글 성공 메시지
     */
    @PutMapping("/{meetupId}/alarm")
    public ResponseEntity<String> toggleMeetupAlarm(
            @PathVariable Long meetupId, Authentication authentication) {
        participantService.toggleMeetupAlarm(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Meetup Alarm toggled successfully.");
    }

    /**
     * 참여 상태 변경 (승인/거절)
     *
     * @param request 참여 상태 변경 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 상태 변경 성공 메시지
     */
    @PutMapping("/status")
    public ResponseEntity<String> approveParticipant(
            @Valid @RequestBody ParticipantStatusRequest request, Authentication authentication) {
        participantService.setHostStatus(request, getUserId(authentication));
        return ResponseEntity.ok("Change Participant Status Successfully.");
    }

    /**
     * Meetup 참여 취소/탈퇴
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 탈퇴 성공 메시지
     */
    @PutMapping("/{meetupId}")
    public ResponseEntity<String> changeParticipant(
            @PathVariable Long meetupId, Authentication authentication) {
        participantService.withdraw(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Withdraw Participant Successfully.");
    }

    /**
     * 참여자의 역할 변경 (예: HOST -> MEMBER)
     *
     * @param request 역할 변경 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 역할 변경 성공 메시지
     */
    @PutMapping("/{meetupId}/role")
    public ResponseEntity<String> changeRole(
            @Valid @RequestBody ParticipantRoleRequest request, Authentication authentication) {
        participantService.putRoleParticipant(request, getUserId(authentication));
        return ResponseEntity.ok("Member Role Change Successfully.");
    }
}
