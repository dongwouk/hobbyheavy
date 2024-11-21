package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.scheduling.TaskScheduler;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class DynamicScheduleServiceTest {

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private TaskScheduler taskScheduler;

    @MockBean
    private FinalizationService finalizationService;

    private DynamicScheduleService dynamicScheduleService;

    @BeforeEach
    public void setUp() {
        dynamicScheduleService = new DynamicScheduleService(scheduleRepository, taskScheduler, finalizationService);
    }

    @Test
    public void testDependencyInjection() {
        // 각 필드가 주입되었는지 확인
        assertNotNull(dynamicScheduleService, "DynamicScheduleService가 주입되지 않았습니다.");
        assertNotNull(scheduleRepository, "ScheduleRepository가 주입되지 않았습니다.");
        assertNotNull(taskScheduler, "TaskScheduler가 주입되지 않았습니다.");
        assertNotNull(finalizationService, "FinalizationService가 주입되지 않았습니다.");
    }

    @Test
    public void testScheduleFinalization_WhenScheduleIsConfirmed_ShouldSkipScheduling() {
        // Confirmed 상태의 스케줄 생성
        MeetupSchedule confirmedSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .scheduleStatus(MeetupScheduleStatus.CONFIRMED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        // 동적 스케줄링 메서드 호출
        dynamicScheduleService.scheduleFinalization(confirmedSchedule);

        // taskScheduler가 호출되지 않았는지 확인
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testScheduleFinalization_WhenVotingDeadlineIsNull_ShouldSkipScheduling() {
        // 마감 시간이 없는 스케줄 생성
        MeetupSchedule scheduleWithoutDeadline = MeetupSchedule.builder()
                .scheduleId(2L)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .votingDeadline(null)
                .build();

        // 동적 스케줄링 메서드 호출
        dynamicScheduleService.scheduleFinalization(scheduleWithoutDeadline);

        // taskScheduler가 호출되지 않았는지 확인
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testScheduleFinalization_WhenValidSchedule_ShouldScheduleFinalization() {
        // 유효한 스케줄 생성
        MeetupSchedule validSchedule = MeetupSchedule.builder()
                .scheduleId(3L)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        // 동적 스케줄링 메서드 호출
        dynamicScheduleService.scheduleFinalization(validSchedule);

        // taskScheduler가 호출되었는지 확인
        verify(taskScheduler, times(1)).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testInitializeDynamicSchedules() {
        // 기존 스케줄 목록 생성
        MeetupSchedule schedule1 = MeetupSchedule.builder()
                .scheduleId(4L)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        MeetupSchedule schedule2 = MeetupSchedule.builder()
                .scheduleId(5L)
                .scheduleStatus(MeetupScheduleStatus.CONFIRMED)
                .votingDeadline(LocalDateTime.now().plusDays(2))
                .build();

        MeetupSchedule expiredSchedule = MeetupSchedule.builder()
                .scheduleId(6L)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().minusDays(1))
                .build();

        List<MeetupSchedule> schedules = Arrays.asList(schedule1, schedule2, expiredSchedule);

        // 스케줄 저장소의 findAll() 메서드가 스케줄 목록을 반환하도록 설정
        when(scheduleRepository.findAll()).thenReturn(schedules);

        // 동적 스케줄 초기화 메서드 호출
        dynamicScheduleService.initializeDynamicSchedules();

        // 만료되지 않은 스케줄만 스케줄링되었는지 확인
        verify(taskScheduler, atLeast(1)).schedule(any(Runnable.class), any(Date.class));
    }
}
