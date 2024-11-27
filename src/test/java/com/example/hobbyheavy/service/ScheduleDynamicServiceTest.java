package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.ScheduleStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
public class ScheduleDynamicServiceTest {

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private TaskScheduler taskScheduler;

    @MockBean
    private ScheduleConfirmService scheduleConfirmService;

    private ScheduleDynamicService scheduleDynamicService;

    @BeforeEach
    public void setUp() {
        scheduleDynamicService = new ScheduleDynamicService(scheduleRepository, taskScheduler, scheduleConfirmService);
    }

    @Test
    public void testDependencyInjection() {
        // 각 필드가 주입되었는지 확인
        assertNotNull(scheduleDynamicService, "DynamicScheduleService가 주입되지 않았습니다.");
        assertNotNull(scheduleRepository, "ScheduleRepository가 주입되지 않았습니다.");
        assertNotNull(taskScheduler, "TaskScheduler가 주입되지 않았습니다.");
        assertNotNull(scheduleConfirmService, "FinalizationService가 주입되지 않았습니다.");
    }

    @Test
    public void testScheduleFinalization_WhenScheduleIsConfirmed_ShouldSkipScheduling() {
        // Confirmed 상태의 스케줄 생성
        Schedule confirmedSchedule = Schedule.builder()
                .scheduleId(1L)
                .scheduleStatus(ScheduleStatus.CONFIRMED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        // 동적 스케줄링 메서드 호출
        scheduleDynamicService.scheduleFinalization(confirmedSchedule);

        // taskScheduler가 호출되지 않았는지 확인
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testScheduleFinalization_WhenVotingDeadlineIsNull_ShouldSkipScheduling() {
        // 마감 시간이 없는 스케줄 생성
        Schedule scheduleWithoutDeadline = Schedule.builder()
                .scheduleId(2L)
                .scheduleStatus(ScheduleStatus.PROPOSED)
                .votingDeadline(null)
                .build();

        // 동적 스케줄링 메서드 호출
        scheduleDynamicService.scheduleFinalization(scheduleWithoutDeadline);

        // taskScheduler가 호출되지 않았는지 확인
        verify(taskScheduler, never()).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testScheduleFinalization_WhenValidSchedule_ShouldScheduleFinalization() {
        // 유효한 스케줄 생성
        Schedule validSchedule = Schedule.builder()
                .scheduleId(3L)
                .scheduleStatus(ScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        // 동적 스케줄링 메서드 호출
        scheduleDynamicService.scheduleFinalization(validSchedule);

        // taskScheduler가 호출되었는지 확인
        verify(taskScheduler, times(1)).schedule(any(Runnable.class), any(Date.class));
    }

    @Test
    public void testInitializeDynamicSchedules() {
        // 기존 스케줄 목록 생성
        Schedule schedule1 = Schedule.builder()
                .scheduleId(4L)
                .scheduleStatus(ScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().plusDays(1))
                .build();

        Schedule schedule2 = Schedule.builder()
                .scheduleId(5L)
                .scheduleStatus(ScheduleStatus.CONFIRMED)
                .votingDeadline(LocalDateTime.now().plusDays(2))
                .build();

        Schedule expiredSchedule = Schedule.builder()
                .scheduleId(6L)
                .scheduleStatus(ScheduleStatus.PROPOSED)
                .votingDeadline(LocalDateTime.now().minusDays(1))
                .build();

        List<Schedule> schedules = Arrays.asList(schedule1, schedule2, expiredSchedule);

        // 스케줄 저장소의 findAll() 메서드가 스케줄 목록을 반환하도록 설정
        when(scheduleRepository.findAll()).thenReturn(schedules);

        // 동적 스케줄 초기화 메서드 호출
        scheduleDynamicService.initializeDynamicSchedules();

        // 만료되지 않은 스케줄만 스케줄링되었는지 확인
        verify(taskScheduler, atLeast(1)).schedule(any(Runnable.class), any(Date.class));
    }
}
