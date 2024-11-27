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

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ScheduleVoteService scheduleVoteService;
    private final ScheduleConfirmService scheduleConfirmService;

    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to create a new schedule", userId);

        // 현재 사용자의 권한 정보를 로깅
        authentication.getAuthorities().forEach(authority ->
                log.info("User {} has authority: {}", userId, authority.getAuthority())
        );

        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId,
                                                        @RequestParam(required = false) Long notificationId,
                                                        Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID 가져오기
        log.info("User {} is attempting to fetch schedule with ID: {}", userId, scheduleId);

        // 스케줄 조회 (사용자 권한 검증 포함)
        ScheduleResponse response = scheduleService.getSchedule(scheduleId, notificationId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules(Authentication authentication) {
        String userId = authentication.getName(); // 현재 인증된 사용자 ID 가져오기
        log.info("User {} is attempting to fetch all schedules", userId);

        // 사용자가 속한 모든 스케줄 조회
        List<ScheduleResponse> responseList = scheduleService.getAllSchedules(userId);
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to update schedule with ID: {}", userId, scheduleId);
        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, scheduleRequest, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to delete schedule with ID: {}", userId, scheduleId);
        scheduleService.deleteSchedule(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{scheduleId}/vote")
    public ResponseEntity<Void> voteOnSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to vote on schedule with ID: {}", userId, scheduleId);
        scheduleVoteService.voteOnSchedule(scheduleId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{scheduleId}/unvote")
    public ResponseEntity<Void> unvoteOnSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to unvote on schedule with ID: {}", userId, scheduleId);
        scheduleVoteService.removeVoteOnSchedule(scheduleId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scheduleId}/confirm")
    public ResponseEntity<String> confirmSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to confirm schedule with ID: {}", userId, scheduleId);
        scheduleConfirmService.finalizeSchedule(scheduleId, userId);
        return ResponseEntity.ok("스케줄이 확정되었습니다.");
    }

    @PostMapping("/{scheduleId}/cancel")
    public ResponseEntity<String> cancelSchedule(@PathVariable Long scheduleId, @RequestBody @Valid ScheduleCancelRequest cancelRequest, Authentication authentication) {
        String userId = authentication.getName();
        String reason = cancelRequest.getReason();
        log.info("User {} is attempting to cancel schedule with ID: {} for reason: {}", userId, scheduleId, reason);
        scheduleService.cancelSchedule(scheduleId, reason, userId);
        return ResponseEntity.ok("스케줄이 취소되었습니다.");
    }
}
