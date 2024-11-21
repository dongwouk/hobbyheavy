package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.util.DurationParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class ScheduleServiceTest {

    @Autowired
    private ScheduleService scheduleService;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private DynamicScheduleService dynamicScheduleService;

    @MockBean
    private NotificationService notificationService;

    private ScheduleRequest scheduleRequest;

    @BeforeEach
    public void setUp() {
        // 테스트를 위한 ScheduleRequest 객체 초기화
        scheduleRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDateTime.of(2024, 12, 1, 10, 0))
                .location("테스트 장소")
                .activateTime("2시간")
                .build();
    }

    @Test
    public void testCreateSchedule() {
        // String 타입의 activateTime을 Duration으로 변환
        Duration activateDuration = DurationParser.parseDuration(scheduleRequest.getActivateTime());

        // Mock MeetupSchedule 객체 생성 (스케줄 저장 시 반환될 객체)
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(activateDuration)
                .location(scheduleRequest.getLocation())
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)  // 기본 상태 설정
                .build();
        meetupSchedule.setScheduleId(1L);  // Schedule ID 설정

        // scheduleRepository.save() 호출 시, mock된 MeetupSchedule 객체 반환
        Mockito.when(scheduleRepository.save(any(MeetupSchedule.class))).thenReturn(meetupSchedule);

        // ScheduleService의 createSchedule() 메서드 호출
        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest, "testUser");

        // 생성된 스케줄 응답 객체 검증
        assertThat(response).isNotNull();  // 응답 객체가 null이 아님
        assertThat(response.getScheduleId()).isEqualTo(1L);  // 스케줄 ID가 1L인지 확인
    }

    @Test
    public void testGetSchedule() {
        // Mock된 MeetupSchedule 객체 생성 (스케줄 조회 시 반환될 객체)
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location(scheduleRequest.getLocation())
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        // scheduleRepository.findById() 호출 시, mock된 MeetupSchedule 객체 반환
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(meetupSchedule));

        // ScheduleService의 getSchedule() 메서드 호출
        ScheduleResponse response = scheduleService.getSchedule(1L);

        // 조회된 스케줄 응답 객체 검증
        assertThat(response).isNotNull();  // 응답 객체가 null이 아님
        assertThat(response.getScheduleId()).isEqualTo(1L);  // 스케줄 ID가 1L인지 확인
        assertThat(response.getLocation()).isEqualTo("테스트 장소");  // 장소가 "테스트 장소"인지 확인
    }

    @Test
    public void testGetAllSchedules() {
        // Mock된 두 개의 MeetupSchedule 객체 생성 (모든 스케줄 조회 시 반환될 리스트)
        MeetupSchedule schedule1 = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location(scheduleRequest.getLocation())
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        MeetupSchedule schedule2 = MeetupSchedule.builder()
                .scheduleId(2L)
                .meetup(Meetup.builder().meetupId(2L).build())
                .proposalDate(scheduleRequest.getProposalDate().plusDays(1))
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location("다른 장소")
                .scheduleStatus(MeetupScheduleStatus.CONFIRMED)
                .build();

        // scheduleRepository.findAll() 호출 시, mock된 리스트 반환
        Mockito.when(scheduleRepository.findAll()).thenReturn(java.util.Arrays.asList(schedule1, schedule2));

        // ScheduleService의 getAllSchedules() 메서드 호출
        java.util.List<ScheduleResponse> responses = scheduleService.getAllSchedules();

        // 조회된 스케줄 응답 객체 리스트 검증
        assertThat(responses).isNotEmpty();  // 응답 리스트가 비어있지 않음
        assertThat(responses.size()).isEqualTo(2);  // 스케줄 개수가 2개인지 확인
        assertThat(responses.get(0).getScheduleId()).isEqualTo(1L);  // 첫 번째 스케줄 ID 확인
        assertThat(responses.get(1).getLocation()).isEqualTo("다른 장소");  // 두 번째 스케줄 장소 확인
    }

    @Test
    public void testUpdateSchedule() {
        // Mock 기존 스케줄 객체 생성 (스케줄 수정 전 객체)
        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location("초기 장소")
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        // scheduleRepository.findById() 호출 시, mock된 MeetupSchedule 객체 반환
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingSchedule));

        // scheduleRepository.save() 호출 시, 수정된 MeetupSchedule 객체 반환
        existingSchedule.setLocation("수정된 장소");
        Mockito.when(scheduleRepository.save(any(MeetupSchedule.class))).thenReturn(existingSchedule);

        // ScheduleRequest 객체 생성 (수정된 데이터 포함)
        ScheduleRequest updatedRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDateTime.of(2024, 12, 2, 10, 0))
                .location("수정된 장소")
                .activateTime("3시간")
                .build();

        // ScheduleService의 updateSchedule() 메서드 호출
        ScheduleResponse response = scheduleService.updateSchedule(1L, updatedRequest, "testUser");

        // 수정된 스케줄 응답 객체 검증
        assertThat(response).isNotNull();  // 응답 객체가 null이 아님
        assertThat(response.getLocation()).isEqualTo("수정된 장소");  // 수정된 장소 확인
    }

    @Test
    public void testDeleteSchedule() {
        // Mock 기존 스케줄 객체 생성 (삭제 전 객체)
        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location(scheduleRequest.getLocation())
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        // scheduleRepository.findById() 호출 시, mock된 MeetupSchedule 객체 반환
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingSchedule));

        // ScheduleService의 deleteSchedule() 메서드 호출
        scheduleService.deleteSchedule(1L, "testUser");

        // 삭제된 상태 확인 (논리적 삭제)
        assertThat(existingSchedule.isDeleted()).isTrue();  // 삭제된 상태 확인
    }

    @Test
    public void testCancelSchedule() {
        // Mock 기존 스케줄 객체 생성 (취소 전 객체)
        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(scheduleRequest.getMeetupId()).build())
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(DurationParser.parseDuration(scheduleRequest.getActivateTime()))
                .location(scheduleRequest.getLocation())
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        // scheduleRepository.findById() 호출 시, mock된 MeetupSchedule 객체 반환
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(java.util.Optional.of(existingSchedule));

        // ScheduleService의 cancelSchedule() 메서드 호출
        scheduleService.cancelSchedule(1L, "테스트 취소 사유", "testUser");

        // 취소된 스케줄 상태와 취소 사유 확인
        assertThat(existingSchedule.getScheduleStatus()).isEqualTo(MeetupScheduleStatus.CANCELLED);  // 스케줄 상태가 취소됨
        assertThat(existingSchedule.getCancellationReason()).isEqualTo("테스트 취소 사유");  // 취소 사유 확인
    }

}
