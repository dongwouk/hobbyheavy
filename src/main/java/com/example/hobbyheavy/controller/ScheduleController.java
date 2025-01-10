package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.request.ScheduleCancelRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.service.ScheduleConfirmService;
import com.example.hobbyheavy.service.ScheduleService;
import com.example.hobbyheavy.service.ScheduleVoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 스케줄(Schedule) 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/schedules") // Schedule API 기본 경로
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
@Slf4j // 로깅을 위한 Lombok 어노테이션
public class ScheduleController {

    private final ScheduleService scheduleService; // 스케줄 관련 비즈니스 로직 처리
    private final ScheduleVoteService scheduleVoteService; // 스케줄 투표 관련 로직 처리
    private final ScheduleConfirmService scheduleConfirmService; // 스케줄 확정 관련 로직 처리

    /**
     * 새로운 스케줄 생성
     *
     * @param scheduleRequest 스케줄 생성 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 생성된 스케줄 응답
     */
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to create a new schedule", userId);

        // 사용자의 권한 로깅
        authentication.getAuthorities().forEach(authority ->
                log.info("User {} has authority: {}", userId, authority.getAuthority())
        );

        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest, userId); // 스케줄 생성
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * 특정 스케줄 조회
     *
     * @param scheduleId 스케줄 ID
     * @param notificationId 알림 ID (선택적)
     * @param authentication Spring Security 인증 객체
     * @return 스케줄 응답
     */
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId,
                                                        @RequestParam(required = false) Long notificationId,
                                                        Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to fetch schedule with ID: {}", userId, scheduleId);

        ScheduleResponse response = scheduleService.getSchedule(scheduleId, notificationId, userId); // 스케줄 조회
        return ResponseEntity.ok(response);
    }

    /**
     * 사용자가 속한 모든 스케줄 조회
     *
     * @param authentication Spring Security 인증 객체
     * @return 스케줄 응답 목록
     */
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules(Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to fetch all schedules", userId);

        List<ScheduleResponse> responseList = scheduleService.getAllSchedules(userId); // 모든 스케줄 조회
        return ResponseEntity.ok(responseList);
    }

    /**
     * 스케줄 수정
     *
     * @param scheduleId 스케줄 ID
     * @param scheduleRequest 스케줄 수정 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 수정된 스케줄 응답
     */
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to update schedule with ID: {}", userId, scheduleId);

        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, scheduleRequest, userId); // 스케줄 수정
        return ResponseEntity.ok(response);
    }

    /**
     * 스케줄 삭제
     *
     * @param scheduleId 스케줄 ID
     * @param authentication Spring Security 인증 객체
     * @return No Content 응답
     */
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to delete schedule with ID: {}", userId, scheduleId);

        scheduleService.deleteSchedule(scheduleId, userId); // 스케줄 삭제
        return ResponseEntity.noContent().build();
    }

    /**
     * 스케줄 투표
     *
     * @param scheduleId 스케줄 ID
     * @param authentication Spring Security 인증 객체
     * @return 생성 성공 응답
     */
    @PostMapping("/{scheduleId}/vote")
    public ResponseEntity<Void> voteOnSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to vote on schedule with ID: {}", userId, scheduleId);

        scheduleVoteService.voteOnSchedule(scheduleId, userId); // 스케줄 투표
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 스케줄 투표 취소
     *
     * @param scheduleId 스케줄 ID
     * @param authentication Spring Security 인증 객체
     * @return 성공 응답
     */
    @PostMapping("/{scheduleId}/unvote")
    public ResponseEntity<Void> unvoteOnSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to unvote on schedule with ID: {}", userId, scheduleId);

        scheduleVoteService.removeVoteOnSchedule(scheduleId, userId); // 스케줄 투표 취소
        return ResponseEntity.ok().build();
    }

    /**
     * 스케줄 확정
     *
     * @param scheduleId 스케줄 ID
     * @param authentication Spring Security 인증 객체
     * @return 확정 성공 메시지
     */
    @PostMapping("/{scheduleId}/confirm")
    public ResponseEntity<String> confirmSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        log.info("User {} is attempting to confirm schedule with ID: {}", userId, scheduleId);

        scheduleConfirmService.finalizeSchedule(scheduleId, userId); // 스케줄 확정
        return ResponseEntity.ok("스케줄이 확정되었습니다.");
    }

    /**
     * 스케줄 취소
     *
     * @param scheduleId 스케줄 ID
     * @param cancelRequest 취소 사유 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 취소 성공 메시지
     */
    @PostMapping("/{scheduleId}/cancel")
    public ResponseEntity<String> cancelSchedule(@PathVariable Long scheduleId, @RequestBody @Valid ScheduleCancelRequest cancelRequest, Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID
        String reason = cancelRequest.getReason(); // 취소 사유
        log.info("User {} is attempting to cancel schedule with ID: {} for reason: {}", userId, scheduleId, reason);

        scheduleService.cancelSchedule(scheduleId, reason, userId); // 스케줄 취소
        return ResponseEntity.ok("스케줄이 취소되었습니다.");
    }
}
