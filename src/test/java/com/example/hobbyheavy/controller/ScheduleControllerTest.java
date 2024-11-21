package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.CancelScheduleRequest;
import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.service.ScheduleService;
import com.example.hobbyheavy.service.VoteService;
import com.example.hobbyheavy.service.FinalizationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ScheduleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @MockBean
    private VoteService voteService;

    @MockBean
    private FinalizationService finalizationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"HOST", "MEMBER"})
    void createSchedule_shouldReturnCreated() throws Exception {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setProposalDate(LocalDateTime.of(2024, 12, 1, 10, 0));
        scheduleRequest.setLocation("Seoul");

        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setScheduleId(1L);
        scheduleResponse.setLocation("Seoul");

        Mockito.when(scheduleService.createSchedule(any(ScheduleRequest.class), any(String.class)))
                .thenReturn(scheduleResponse);

        mockMvc.perform(MockMvcRequestBuilders.post("/schedules")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scheduleId").value(1L));
    }

    @Test
    @WithMockUser(roles = {"HOST", "MEMBER"})
    void getSchedule_shouldReturnOk() throws Exception {
        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setScheduleId(1L);
        scheduleResponse.setLocation("Seoul");

        Mockito.when(scheduleService.getSchedule(1L)).thenReturn(scheduleResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/schedules/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.scheduleId").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Seoul"));
    }

    @Test
    @WithMockUser(roles = {"HOST"})
    void updateSchedule_shouldReturnOk() throws Exception {
        ScheduleRequest scheduleRequest = new ScheduleRequest();
        scheduleRequest.setProposalDate(LocalDateTime.of(2024, 12, 1, 12, 0));
        scheduleRequest.setLocation("Busan");

        ScheduleResponse scheduleResponse = new ScheduleResponse();
        scheduleResponse.setScheduleId(1L);
        scheduleResponse.setLocation("Busan");

        Mockito.when(scheduleService.updateSchedule(any(Long.class), any(ScheduleRequest.class), any(String.class)))
                .thenReturn(scheduleResponse);

        mockMvc.perform(MockMvcRequestBuilders.put("/schedules/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(scheduleRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.location").value("Busan"));
    }

    @Test
    @WithMockUser(roles = {"HOST"})
    void deleteSchedule_shouldReturnNoContent() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/schedules/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"HOST"})
    void cancelSchedule_shouldReturnOk() throws Exception {
        CancelScheduleRequest cancelRequest = new CancelScheduleRequest();
        cancelRequest.setReason("Scheduling conflict");

        mockMvc.perform(MockMvcRequestBuilders.post("/schedules/1/cancel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cancelRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("스케줄이 취소되었습니다."));
    }
}
