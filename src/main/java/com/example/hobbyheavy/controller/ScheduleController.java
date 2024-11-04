package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequestDTO;
import com.example.hobbyheavy.dto.request.ScheduleUpdateDTO;
import com.example.hobbyheavy.dto.request.ScheduleVoteDTO;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.entity.Vote;
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

    // 새로운 일정 생성
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@RequestBody ScheduleRequestDTO requestDTO) {
        Schedule schedule = scheduleService.createSchedule(requestDTO);
        return new ResponseEntity<>(schedule, HttpStatus.CREATED);
    }

    // 기존 일정 업데이트
    @PutMapping("/{scheduleId}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long scheduleId, @RequestBody ScheduleUpdateDTO updateDTO) {
        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, updateDTO);
        return new ResponseEntity<>(updatedSchedule, HttpStatus.OK);
    }

    // 특정 모임의 모든 일정 조회
    @GetMapping("/meetup/{meetupId}")
    public ResponseEntity<List<Schedule>> getSchedulesForMeetup(@PathVariable Long meetupId) {
        List<Schedule> schedules = scheduleService.getSchedulesForMeetup(meetupId);
        return new ResponseEntity<>(schedules, HttpStatus.OK);
    }

    // 특정 일정에 대한 투표
    @PostMapping("/vote")
    public ResponseEntity<Vote> castVote(@RequestBody ScheduleVoteDTO voteDTO) {
        Vote vote = scheduleService.castVote(voteDTO);
        return new ResponseEntity<>(vote, HttpStatus.CREATED);
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long scheduleId) {
        scheduleService.deleteSchedule(scheduleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
