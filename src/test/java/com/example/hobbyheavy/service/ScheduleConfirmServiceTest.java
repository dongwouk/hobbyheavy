package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.ScheduleStatus;
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
public class ScheduleConfirmServiceTest {

    @Autowired
    private ScheduleConfirmService scheduleConfirmService;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private ParticipantRepository participantRepository;

    @MockBean
    private NotificationService notificationService;

    private Schedule schedule;

    @BeforeEach
    public void setUp() {
        Meetup meetup = Meetup.builder().meetupId(1L).build();
        schedule = Schedule.builder()
                .scheduleId(1L)
                .meetup(meetup)
                .scheduleStatus(ScheduleStatus.PROPOSED)
                .build();
    }

    @Test
    public void testFinalizeScheduleSuccess() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Participant participant = Participant.builder()
                .meetupRole("HOST")
                .meetup(schedule.getMeetup())
                .user(null)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(eq(1L), eq("hostUser"))).thenReturn(Optional.of(participant));

        scheduleConfirmService.finalizeSchedule(1L, "hostUser");

        Mockito.verify(scheduleRepository).save(schedule);
        Mockito.verify(notificationService).notifyScheduleConfirmation(schedule);
    }

    @Test
    public void testScheduleNotFoundException() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> scheduleConfirmService.finalizeSchedule(1L, "hostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.SCHEDULE_NOT_FOUND.getMessage());
    }

    @Test
    public void testUserForbiddenException() {
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        Participant participant = Participant.builder()
                .meetupRole("MEMBER")  // 권한 없는 사용자
                .meetup(schedule.getMeetup())
                .user(null)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(eq(1L), eq("nonHostUser"))).thenReturn(Optional.of(participant));

        assertThatThrownBy(() -> scheduleConfirmService.finalizeSchedule(1L, "nonHostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.FORBIDDEN_ACTION.getMessage());
    }

    @Test
    public void testScheduleAlreadyConfirmedException() {
        schedule.setScheduleStatus(ScheduleStatus.CONFIRMED);
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(schedule));

        assertThatThrownBy(() -> scheduleConfirmService.finalizeSchedule(1L, "hostUser"))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(ExceptionCode.SCHEDULE_ALREADY_CONFIRMED.getMessage());
    }
}
