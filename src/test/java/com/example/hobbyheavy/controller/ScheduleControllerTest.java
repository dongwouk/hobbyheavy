package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ScheduleRequestDTO;
import com.example.hobbyheavy.dto.request.ScheduleUpdateDTO;
import com.example.hobbyheavy.dto.request.ScheduleVoteDTO;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.entity.Vote;
import com.example.hobbyheavy.service.ScheduleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ScheduleController.class)
public class ScheduleControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ScheduleService scheduleService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
    }

    // 새로운 일정 생성 테스트
    @Test
    void testCreateSchedule() throws Exception {
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

        when(scheduleService.createSchedule(any(ScheduleRequestDTO.class))).thenReturn(schedule);

        mockMvc.perform(post("/api/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO))
                        .with(jwt().jwt(builder -> builder.claim("sub", "user-id").claim("scope", "ROLE_USER")))) // JWT 추가
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.scheduleId").value(1L))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    // 일정 업데이트 테스트
    @Test
    void testUpdateSchedule() throws Exception {
        Long scheduleId = 1L;
        ScheduleUpdateDTO updateDTO = ScheduleUpdateDTO.builder()
                .status("CONFIRMED")
                .votes(10)
                .build();

        Schedule updatedSchedule = Schedule.builder()
                .scheduleId(scheduleId)
                .status("CONFIRMED")
                .votes(10)
                .build();

        when(scheduleService.updateSchedule(anyLong(), any(ScheduleUpdateDTO.class))).thenReturn(updatedSchedule);

        mockMvc.perform(put("/api/schedules/{scheduleId}", scheduleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO))
                        .with(jwt().jwt(builder -> builder.subject("user-id").claim("scope", "ROLE_USER")))) // JWT 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.scheduleId").value(1L))
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.votes").value(10));
    }

    // 특정 모임의 모든 일정 조회 테스트
    @Test
    void testGetSchedulesForMeetup() throws Exception {
        Long meetupId = 1L;
        Schedule schedule1 = Schedule.builder()
                .scheduleId(1L)
                .status("PENDING")
                .build();
        Schedule schedule2 = Schedule.builder()
                .scheduleId(2L)
                .status("CONFIRMED")
                .build();

        when(scheduleService.getSchedulesForMeetup(anyLong())).thenReturn(Arrays.asList(schedule1, schedule2));

        mockMvc.perform(get("/api/schedules/meetup/{meetupId}", meetupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(jwt().jwt(builder -> builder.subject("user-id").claim("scope", "ROLE_USER")))) // JWT 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].scheduleId").value(1L))
                .andExpect(jsonPath("$[0].status").value("PENDING"))
                .andExpect(jsonPath("$[1].scheduleId").value(2L))
                .andExpect(jsonPath("$[1].status").value("CONFIRMED"));
    }

    // 일정에 대한 투표 테스트
    @Test
    void testCastVote() throws Exception {
        ScheduleVoteDTO voteDTO = ScheduleVoteDTO.builder()
                .scheduleId(1L)
                .user(null) // 실제로 필요한 경우 User 객체 설정
                .build();

        Vote vote = Vote.builder()
                .voteId(1L)
                .voteTimestamp(LocalDateTime.now())
                .build();

        when(scheduleService.castVote(any(ScheduleVoteDTO.class))).thenReturn(vote);

        mockMvc.perform(post("/api/schedules/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteDTO))
                        .with(jwt().jwt(builder -> builder.subject("user-id").claim("scope", "ROLE_USER")))) // JWT 추가
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.voteId").value(1L));
    }

    // 일정 삭제 테스트
    @Test
    void testDeleteSchedule() throws Exception {
        Long scheduleId = 1L;

        mockMvc.perform(delete("/api/schedules/{scheduleId}", scheduleId)
                        .with(jwt().jwt(builder -> builder.subject("user-id").claim("scope", "ROLE_USER")))) // JWT 추가
                .andExpect(status().isNoContent());
    }
}
