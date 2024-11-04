package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequestDTO;
import com.example.hobbyheavy.dto.request.ScheduleUpdateDTO;
import com.example.hobbyheavy.dto.request.ScheduleVoteDTO;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.entity.Vote;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final VoteRepository voteRepository;

    // 새로운 일정 생성
    @Transactional
    public Schedule createSchedule(ScheduleRequestDTO requestDTO) {
        Schedule schedule = Schedule.builder()
                .meetup(requestDTO.getMeetup())
                .date(requestDTO.getDate())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .status("PENDING")
                .createdAt(LocalDateTime.now())
                .build();
        return scheduleRepository.save(schedule);
    }

    // 기존 일정 업데이트
    @Transactional
    public Schedule updateSchedule(Long scheduleId, ScheduleUpdateDTO updateDTO) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));

        schedule.updateStatus(updateDTO.getStatus());
        schedule.updateVotes(updateDTO.getVotes());
        schedule.updateUpdatedAt(LocalDateTime.now());
        return scheduleRepository.save(schedule);
    }

    // 특정 모임의 모든 일정 조회
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesForMeetup(Long meetupId) {
        return scheduleRepository.findByMeetupId(meetupId);
    }

    // 특정 일정에 대한 투표
    @Transactional
    public Vote castVote(ScheduleVoteDTO voteDTO) {
        Schedule schedule = scheduleRepository.findById(voteDTO.getScheduleId())
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + voteDTO.getScheduleId()));

        Vote vote = Vote.builder()
                .schedule(schedule)
                .user(voteDTO.getUser())
                .voteTimestamp(LocalDateTime.now())
                .build();
        return voteRepository.save(vote);
    }

    // 일정 삭제
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("Schedule not found with id: " + scheduleId));
        scheduleRepository.delete(schedule);
    }
}
