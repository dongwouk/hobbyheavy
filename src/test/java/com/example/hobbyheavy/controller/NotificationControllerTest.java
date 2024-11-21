package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.NotificationResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.NotificationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class NotificationControllerTest {

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private NotificationService notificationService;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = {"USER"})
    public void getUserNotifications_shouldReturnOk() throws Exception {
        // MockMvc setup
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // given
        NotificationResponse notification1 = NotificationResponse.builder()
                .notificationId(1L)
                .userId("testUser")
                .message("New Meetup Created")
                .type(null) // Assuming NotificationType is not needed for this test
                .isRead(false)
                .createdDate(LocalDateTime.now())
                .build();
        NotificationResponse notification2 = NotificationResponse.builder()
                .notificationId(2L)
                .userId("testUser")
                .message("Meetup Schedule Updated")
                .type(null) // Assuming NotificationType is not needed for this test
                .isRead(true)
                .createdDate(LocalDateTime.now())
                .build();
        List<NotificationResponse> notifications = Arrays.asList(notification1, notification2);

        Mockito.when(notificationService.getUserNotifications("testUser")).thenReturn(notifications);

        // when, then
        mockMvc.perform(get("/notifications")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notificationId").value(1L))
                .andExpect(jsonPath("$[0].message").value("New Meetup Created"))
                .andExpect(jsonPath("$[0].isRead").value(false))
                .andExpect(jsonPath("$[1].notificationId").value(2L))
                .andExpect(jsonPath("$[1].message").value("Meetup Schedule Updated"))
                .andExpect(jsonPath("$[1].isRead").value(true));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void markNotificationAsRead_shouldReturnOk() throws Exception {
        // MockMvc setup
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // given
        Mockito.doNothing().when(notificationService).markAsRead(anyLong());

        // when, then
        mockMvc.perform(put("/notifications/1/mark-as-read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Notification marked as read"));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void markNotificationAsRead_shouldReturnNotFound() throws Exception {
        // MockMvc setup
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // given
        Mockito.doThrow(new CustomException(ExceptionCode.NOTIFICATION_NOT_FOUND)).when(notificationService).markAsRead(anyLong());

        // when, then
        mockMvc.perform(put("/notifications/1/mark-as-read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("해당 알림을 찾을 수 없습니다."));
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void markNotificationAsRead_shouldReturnServerError() throws Exception {
        // MockMvc setup
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();

        // given
        Mockito.doThrow(new CustomException(ExceptionCode.INTERNAL_SERVER_ERROR)).when(notificationService).markAsRead(anyLong());

        // when, then
        mockMvc.perform(put("/notifications/1/mark-as-read")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("서버 내부 오류가 발생했습니다."));
    }
}
