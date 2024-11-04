package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequestDTO;
import com.example.hobbyheavy.dto.request.ScheduleUpdateDTO;
import com.example.hobbyheavy.dto.request.ScheduleVoteDTO;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.entity.Vote;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.repository.VoteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private VoteRepository voteRepository;

    // 새로운 일정 생성 테스트
    @Test
    void testCreateSchedule() {
        ScheduleRequestDTO requestDTO = ScheduleRequestDTO.builder()
                .meetup(null) // 실제로 필요한 경우 Meetup 객체 설정
                .date(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(12, 0))
                .build();

        Schedule schedule = Schedule.builder()
                .scheduleId(1L)
                .date(requestDTO.getDate())
                .startTime(requestDTO.getStartTime())
                .endTime(requestDTO.getEndTime())
                .status("PENDING")
                .build();

        when(scheduleRepository.save(any(Schedule.class))).thenReturn(schedule);

        Schedule createdSchedule = scheduleService.createSchedule(requestDTO);

        assertNotNull(createdSchedule);
        assertEquals("PENDING", createdSchedule.getStatus());
        verify(scheduleRepository, times(1)).save(any(Schedule.class));
    }

    // 일정 업데이트 테스트
    @Test
    void testUpdateSchedule() {
        Long scheduleId = 1L;
        ScheduleUpdateDTO updateDTO = ScheduleUpdateDTO.builder()
                .status("CONFIRMED")
                .votes(10)
                .build();

        Schedule existingSchedule = Schedule.builder()
                .scheduleId(scheduleId)
                .status("PENDING")
                .votes(5)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(any(Schedule.class))).thenReturn(existingSchedule);

        Schedule updatedSchedule = scheduleService.updateSchedule(scheduleId, updateDTO);

        assertNotNull(updatedSchedule);
        assertEquals("CONFIRMED", updatedSchedule.getStatus());
        assertEquals(10, updatedSchedule.getVotes());
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    // 일정 조회 실패 테스트
    @Test
    void testUpdateScheduleNotFound() {
        Long scheduleId = 1L;
        ScheduleUpdateDTO updateDTO = ScheduleUpdateDTO.builder()
                .status("CONFIRMED")
                .votes(10)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.updateSchedule(scheduleId, updateDTO));
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    // 일정에 대한 투표 테스트
    @Test
    void testCastVote() {
        Long scheduleId = 1L;
        ScheduleVoteDTO voteDTO = ScheduleVoteDTO.builder()
                .scheduleId(scheduleId)
                .user(null) // 실제로 필요한 경우 User 객체 설정
                .build();

        Schedule schedule = Schedule.builder()
                .scheduleId(scheduleId)
                .status("PENDING")
                .build();

        Vote vote = Vote.builder()
                .voteId(1L)
                .schedule(schedule)
                .voteTimestamp(LocalDateTime.now())
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(voteRepository.save(any(Vote.class))).thenReturn(vote);

        Vote castedVote = scheduleService.castVote(voteDTO);

        assertNotNull(castedVote);
        assertEquals(schedule, castedVote.getSchedule());
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(voteRepository, times(1)).save(any(Vote.class));
    }
}
