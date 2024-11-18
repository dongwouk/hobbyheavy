package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.service.DynamicScheduleService;
import com.example.hobbyheavy.service.ScheduleService;
import com.example.hobbyheavy.service.VoteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final VoteService voteService;
    private final DynamicScheduleService dynamicScheduleService;

    /**
     * 새로운 모임 스케줄을 생성합니다.
     *
     * @param scheduleRequest 생성할 스케줄의 요청 데이터
     * @return 생성된 스케줄의 응답 데이터
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ScheduleResponse createSchedule(@Valid @RequestBody ScheduleRequest scheduleRequest) {
        return scheduleService.createSchedule(scheduleRequest);
    }

    /**
     * 특정 모임 스케줄을 조회합니다.
     *
     * @param scheduleId 조회할 스케줄의 ID
     * @return 조회된 스케줄의 응답 데이터
     */
    @GetMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleResponse getSchedule(@PathVariable Long scheduleId) {
        return scheduleService.getSchedule(scheduleId);
    }

    /**
     * 모든 모임 스케줄을 조회합니다.
     *
     * @return 모든 스케줄의 응답 데이터 목록
     */
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ScheduleResponse> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    /**
     * 특정 모임 스케줄을 수정합니다.
     *
     * @param scheduleId      수정할 스케줄의 ID
     * @param scheduleRequest 수정할 스케줄의 요청 데이터
     * @return 수정된 스케줄의 응답 데이터
     */
    @PutMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.OK)
    public ScheduleResponse updateSchedule(@PathVariable Long scheduleId, @Valid @RequestBody ScheduleRequest scheduleRequest) {
        return scheduleService.updateSchedule(scheduleId, scheduleRequest);
    }

    /**
     * 특정 모임 스케줄을 삭제합니다.
     *
     * @param scheduleId 삭제할 스케줄의 ID
     */
    @DeleteMapping("/{scheduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
    }

    /**
     * 특정 스케줄에 대해 투표를 진행합니다.
     *
     * @param scheduleId 투표할 스케줄의 ID
     */
    @PostMapping("/{scheduleId}/vote")
    @ResponseStatus(HttpStatus.CREATED)
    public void voteOnSchedule(@PathVariable Long scheduleId) {
        voteService.voteOnSchedule(scheduleId);
    }

    /**
     * 특정 모임 스케줄을 확정합니다.
     *
     * @param scheduleId 확정할 스케줄의 ID
     * @return 스케줄 확정 완료 메시지
     */
    @PostMapping("/{scheduleId}/confirm")
    @ResponseStatus(HttpStatus.OK)
    public String confirmSchedule(@PathVariable Long scheduleId) {
        dynamicScheduleService.finalizeSchedule(scheduleId);
        return "스케줄이 확정되었습니다.";
    }

    /**
     * 특정 모임 스케줄을 취소합니다.
     *
     * @param scheduleId 취소할 스케줄의 ID
     * @param reason     스케줄 취소 이유
     * @return 스케줄 취소 완료 메시지
     */
    @PostMapping("/{scheduleId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public String cancelSchedule(@PathVariable Long scheduleId, @RequestBody String reason) {
        scheduleService.cancelSchedule(scheduleId, reason);
        return "스케줄이 취소되었습니다.";
    }
}
