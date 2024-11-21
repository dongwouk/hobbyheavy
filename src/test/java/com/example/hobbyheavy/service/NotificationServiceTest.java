package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.NotificationResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Notification;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.repository.NotificationRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.type.NotificationMessage;
import com.example.hobbyheavy.type.NotificationType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationServiceTest {

    @Autowired
    private NotificationService notificationService;

    @MockBean
    private ParticipantRepository participantRepository;

    @MockBean
    private NotificationSender notificationSender;

    @MockBean
    private NotificationRepository notificationRepository;

    private MeetupSchedule meetupSchedule;
    private User user;
    private Participant participant;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .userId("testUser")
                .username("Test User")
                .email("testuser@example.com")
                .build();

        Meetup meetup = Meetup.builder()
                .meetupId(1L)
                .meetupName("Test Meetup")
                .build();

        meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(meetup)
                .build();

        participant = Participant.builder()
                .participantId(1L)
                .user(user)
                .meetup(meetup)
                .build();
    }

    @Test
    public void testNotifyParticipants_WithParticipants() {
        // Mock 설정: 참여자 목록 반환
        Mockito.when(participantRepository.findAllByMeetup_MeetupId(1L))
                .thenReturn(Collections.singletonList(participant));

        // 알림 저장 mock 설정
        when(notificationRepository.save(any(Notification.class))).thenReturn(Notification.builder().build());

        // 알림 발송 메서드 호출
        notificationService.notifyParticipants(meetupSchedule, NotificationMessage.SCHEDULE_CREATION);

        // 알림 발송 및 저장이 각각 한 번씩 호출되었는지 확인
        verify(notificationSender, times(1)).send(anyString(), anyString());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    public void testNotifyParticipants_NoParticipants() {
        // Mock 설정: 빈 참여자 목록 반환
        Mockito.when(participantRepository.findAllByMeetup_MeetupId(1L))
                .thenReturn(Collections.emptyList());

        // 참여자가 없을 때 예외 발생 확인
        assertThatThrownBy(() -> notificationService.notifyParticipants(meetupSchedule, NotificationMessage.SCHEDULE_CREATION))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("참여자가 없습니다.");
    }

    @Test
    public void testSendNotification_Success() {
        // 알림 발송 메서드 호출
        notificationService.sendNotification(participant, "Test Message");

        // 알림 발송이 한 번 호출되었는지 확인
        verify(notificationSender, times(1)).send(participant.getUser().getEmail(), "Test Message");
    }

    @Test
    public void testSendNotification_Failure() {
        // Mock 설정: 알림 발송 실패 시 예외 발생
        doThrow(new RuntimeException("Email send failure"))
                .when(notificationSender)
                .send(anyString(), anyString());

        // 알림 발송 메서드 호출 시 예외 처리 확인
        notificationService.sendNotification(participant, "Test Message");

        // 실패 처리 메서드 호출 여부 확인
        verify(notificationSender, times(1)).send(participant.getUser().getEmail(), "Test Message");
    }

    @Test
    public void testMarkAsRead() {
        // Mock 설정: 알림 객체 반환
        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .isRead(false)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // 알림 읽음 처리 메서드 호출
        notificationService.markAsRead(1L);

        // 읽음 처리 확인
        assertThat(notification.getIsRead()).isTrue();
        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    public void testMarkAsRead_AlreadyRead() {
        // Mock 설정: 이미 읽은 알림 객체 반환
        Notification notification = Notification.builder()
                .notificationId(1L)
                .user(user)
                .isRead(true)
                .build();

        when(notificationRepository.findById(1L)).thenReturn(Optional.of(notification));

        // 알림 읽음 처리 메서드 호출 (이미 읽음 상태)
        notificationService.markAsRead(1L);

        // 읽음 상태가 유지되었는지 확인
        assertThat(notification.getIsRead()).isTrue();
        verify(notificationRepository, never()).save(notification);
    }

    @Test
    public void testGetUserNotifications() {
        // Mock 설정: 알림 목록 반환
        Notification notification1 = Notification.builder()
                .notificationId(1L)
                .user(user)
                .message("Test Message 1")
                .type(NotificationType.SCHEDULE_CREATION)
                .isRead(false)
                .build();

        Notification notification2 = Notification.builder()
                .notificationId(2L)
                .user(user)
                .message("Test Message 2")
                .type(NotificationType.SCHEDULE_CONFIRMATION)
                .isRead(true)
                .build();

        when(notificationRepository.findAllByUser_UserId("testUser")).thenReturn(List.of(notification1, notification2));

        // 사용자 알림 조회 메서드 호출
        List<NotificationResponse> notifications = notificationService.getUserNotifications("testUser");

        // 결과 확인
        assertThat(notifications).isNotEmpty();
        assertThat(notifications.size()).isEqualTo(2);
        assertThat(notifications.get(0).getMessage()).isEqualTo("Test Message 1");
        assertThat(notifications.get(1).getIsRead()).isTrue();
    }
}
