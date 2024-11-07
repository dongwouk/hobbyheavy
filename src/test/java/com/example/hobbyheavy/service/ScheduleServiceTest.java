package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.repository.ScheduleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createSchedule_ShouldReturnCreatedSchedule() {
        // given
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDateTime.now())
                .activateTime(LocalDateTime.now().plusHours(2))
                .status("제안")
                .participant("user1")
                .votes(0)
                .location("Location1")
                .build();

        MeetupSchedule meetupSchedule = scheduleRequest.toEntity();
        when(scheduleRepository.save(any(MeetupSchedule.class))).thenReturn(meetupSchedule);

        // when
        ScheduleResponse scheduleResponse = scheduleService.createSchedule(scheduleRequest);

        // then
        assertNotNull(scheduleResponse);
        assertEquals(scheduleRequest.getLocation(), scheduleResponse.getLocation());
        verify(scheduleRepository, times(1)).save(any(MeetupSchedule.class));
    }

    @Test
    void getSchedule_ShouldReturnSchedule_WhenFound() {
        // given
        Long scheduleId = 1L;
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .location("Location1")
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // when
        ScheduleResponse scheduleResponse = scheduleService.getSchedule(scheduleId);

        // then
        assertNotNull(scheduleResponse);
        assertEquals(scheduleId, scheduleResponse.getScheduleId());
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    @Test
    void getSchedule_ShouldThrowException_WhenNotFound() {
        // given
        Long scheduleId = 1L;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getSchedule(scheduleId));
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    @Test
    void getAllSchedules_ShouldReturnListOfSchedules() {
        // given
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .location("Location1")
                .build();

        when(scheduleRepository.findAll()).thenReturn(Collections.singletonList(meetupSchedule));

        // when
        List<ScheduleResponse> scheduleResponses = scheduleService.getAllSchedules();

        // then
        assertNotNull(scheduleResponses);
        assertFalse(scheduleResponses.isEmpty());
        verify(scheduleRepository, times(1)).findAll();
    }

    @Test
    void updateSchedule_ShouldUpdateAndReturnUpdatedSchedule() {
        // given
        Long scheduleId = 1L;
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .location("Updated Location")
                .build();

        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .location("Old Location")
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));
        when(scheduleRepository.save(existingSchedule)).thenReturn(existingSchedule);

        // when
        ScheduleResponse updatedResponse = scheduleService.updateSchedule(scheduleId, scheduleRequest);

        // then
        assertNotNull(updatedResponse);
        assertEquals(scheduleRequest.getLocation(), updatedResponse.getLocation());
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    @Test
    void deleteSchedule_ShouldDeleteSchedule_WhenFound() {
        // given
        Long scheduleId = 1L;
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // when
        scheduleService.deleteSchedule(scheduleId);

        // then
        verify(scheduleRepository, times(1)).findById(scheduleId);
        verify(scheduleRepository, times(1)).delete(meetupSchedule);
    }

    @Test
    void deleteSchedule_ShouldThrowException_WhenNotFound() {
        // given
        Long scheduleId = 1L;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.deleteSchedule(scheduleId));
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }
}
