package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.request.CancelScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.service.DynamicScheduleService;
import com.example.hobbyheavy.service.FinalizationService;
import com.example.hobbyheavy.service.ScheduleService;
import com.example.hobbyheavy.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
@Slf4j
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final VoteService voteService;
    private final FinalizationService finalizationService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_HOST') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<ScheduleResponse> createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to create a new schedule", userId);
        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_HOST') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId) {
        log.info("Fetching schedule with ID: {}", scheduleId);
        ScheduleResponse response = scheduleService.getSchedule(scheduleId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_HOST') or hasRole('ROLE_MEMBER')")
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        log.info("Fetching all schedules");
        List<ScheduleResponse> responseList = scheduleService.getAllSchedules();
        return ResponseEntity.ok(responseList);
    }

    @PutMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to update schedule with ID: {}", userId, scheduleId);
        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, scheduleRequest, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{scheduleId}")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to delete schedule with ID: {}", userId, scheduleId);
        scheduleService.deleteSchedule(scheduleId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{scheduleId}/vote")
    @PreAuthorize("hasRole('ROLE_MEMBER')")
    public ResponseEntity<Void> voteOnSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to vote on schedule with ID: {}", userId, scheduleId);
        voteService.voteOnSchedule(scheduleId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/{scheduleId}/confirm")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<String> confirmSchedule(@PathVariable Long scheduleId, Authentication authentication) {
        String userId = authentication.getName();
        log.info("User {} is attempting to confirm schedule with ID: {}", userId, scheduleId);
        finalizationService.finalizeSchedule(scheduleId, userId);
        return ResponseEntity.ok("스케줄이 확정되었습니다.");
    }

    @PostMapping("/{scheduleId}/cancel")
    @PreAuthorize("hasRole('ROLE_HOST')")
    public ResponseEntity<String> cancelSchedule(@PathVariable Long scheduleId, @RequestBody @Valid CancelScheduleRequest cancelRequest, Authentication authentication) {
        String userId = authentication.getName();
        String reason = cancelRequest.getReason();
        log.info("User {} is attempting to cancel schedule with ID: {} for reason: {}", userId, scheduleId, reason);
        scheduleService.cancelSchedule(scheduleId, reason, userId);
        return ResponseEntity.ok("스케줄이 취소되었습니다.");
    }
}
