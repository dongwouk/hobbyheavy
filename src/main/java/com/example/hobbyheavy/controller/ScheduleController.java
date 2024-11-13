package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 모임 스케줄 생성
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(@RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse responseDTO = scheduleService.createSchedule(scheduleRequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    // 특정 모임 스케줄 조회
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(@PathVariable Long scheduleId) {
        ScheduleResponse responseDTO = scheduleService.getSchedule(scheduleId);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 모든 모임 스케줄 조회
    @GetMapping
    public ResponseEntity<List<ScheduleResponse>> getAllSchedules() {
        List<ScheduleResponse> responseDTOList = scheduleService.getAllSchedules();
        return new ResponseEntity<>(responseDTOList, HttpStatus.OK);
    }

    // 모임 스케줄 수정
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleRequest scheduleRequest) {
        ScheduleResponse responseDTO = scheduleService.updateSchedule(scheduleId, scheduleRequest);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    // 모임 스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // 모임 스케줄에 투표하기
    @PostMapping("/{scheduleId}/vote")
    public ResponseEntity<Void> voteOnSchedule(@PathVariable Long scheduleId) {
        scheduleService.voteOnSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // 모임 스케줄 확정하기
    @PostMapping("/{scheduleId}/confirm")
    public ResponseEntity<String> confirmSchedule(@PathVariable Long scheduleId) {
        scheduleService.finalizeSchedule(scheduleId);
        return new ResponseEntity<>("스케줄이 확정되었습니다.", HttpStatus.OK);
    }

    // 모임 스케줄 취소하기
    @PostMapping("/{scheduleId}/cancel")
    public ResponseEntity<String> cancelSchedule(@PathVariable Long scheduleId, @RequestBody String reason) {
        scheduleService.cancelSchedule(scheduleId, reason);
        return new ResponseEntity<>("스케줄이 취소되었습니다.", HttpStatus.OK);
    }
}
