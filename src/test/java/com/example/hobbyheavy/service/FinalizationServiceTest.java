package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@SpringBootTest
public class FinalizationServiceTest {

    @Autowired
    private FinalizationService finalizationService;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private ParticipantRepository participantRepository;

    @MockBean
    private NotificationService notificationService;

    private MeetupSchedule meetupSchedule;

    @BeforeEach
    public void setUp() {
        Meetup meetup = Meetup.builder().meetupId(1L).build();
        meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(meetup)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();
    }

    @Test
    public void testFinalizeScheduleSuccess() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));

        Participant participant = Participant.builder()
                .meetupRole("HOST")
                .meetup(meetupSchedule.getMeetup())
                .user(null)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(eq(1L), eq("hostUser"))).thenReturn(Optional.of(participant));

        finalizationService.finalizeSchedule(1L, "hostUser");

        Mockito.verify(scheduleRepository).save(meetupSchedule);
        Mockito.verify(notificationService).notifyScheduleConfirmation(meetupSchedule);
    }

    @Test
    public void testScheduleNotFoundException() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> finalizationService.finalizeSchedule(1L, "hostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.SCHEDULE_NOT_FOUND.getMessage());
    }

    @Test
    public void testUserForbiddenException() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));

        Participant participant = Participant.builder()
                .meetupRole("MEMBER")  // 권한 없는 사용자
                .meetup(meetupSchedule.getMeetup())
                .user(null)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(eq(1L), eq("nonHostUser"))).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> finalizationService.finalizeSchedule(1L, "nonHostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.FORBIDDEN_ACTION.getMessage());
    }

    @Test
    public void testScheduleAlreadyConfirmedException() {
        meetupSchedule.setScheduleStatus(MeetupScheduleStatus.CONFIRMED);
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));

        assertThatThrownBy(() -> finalizationService.finalizeSchedule(1L, "hostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.SCHEDULE_ALREADY_CONFIRMED.getMessage());
    }
}
