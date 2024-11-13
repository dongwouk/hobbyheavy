package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.exception.UnauthorizedException;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class ScheduleServiceTest {

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private TaskScheduler taskScheduler;

    @Mock
    private ScheduledFuture<?> scheduledFuture;

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    // 1. 스케줄 생성 테스트 (성공)
    @Test
    void createSchedule_success() {
        // given
        String userId = "user1";
        Long meetupId = 1L;

        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .meetupId(meetupId)
                .proposalDate(LocalDateTime.of(2024, 11, 16, 15, 0))
                .activateTime("2시간")
                .votingDeadline("3일")
                .location("Busan")
                .build();

        Meetup meetup = Meetup.builder().meetupId(meetupId).build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 호스트 역할로 참가한 상태로 설정
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId))
                .thenReturn(Optional.of(Participant.builder()
                        .user(User.builder().userId(userId).build())
                        .meetup(meetup)
                        .meetupRole(ParticipantRole.HOST.name())
                        .build()));

        // Mock 설정: 스케줄 저장
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .meetup(meetup)
                .proposalDate(scheduleRequest.getProposalDate())
                .activateTime(scheduleService.parseDuration(scheduleRequest.getActivateTime()))
                .votingDeadline(scheduleRequest.getProposalDate().plusDays(3))
                .location(scheduleRequest.getLocation())
                .scheduleId(1L)
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        when(scheduleRepository.save(any(MeetupSchedule.class))).thenReturn(meetupSchedule);

        // when
        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getScheduleId());
        assertEquals("Busan", response.getLocation());
        verify(scheduleRepository, times(1)).save(any(MeetupSchedule.class));
    }

    // 2. 스케줄 조회 테스트 (성공)
    @Test
    void getSchedule_success() {
        // given
        Long scheduleId = 1L;

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .location("Seoul")
                .build();

        // Mock 설정: 스케줄을 조회했을 때 값을 반환하도록 설정
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // when
        ScheduleResponse response = scheduleService.getSchedule(scheduleId);

        // then
        assertNotNull(response);
        assertEquals(scheduleId, response.getScheduleId());
        assertEquals("Seoul", response.getLocation());
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    // 3. 스케줄 조회 테스트 (실패 - 스케줄 없음)
    @Test
    void getSchedule_notFound() {
        // given
        Long scheduleId = 1L;

        // Mock 설정: 스케줄을 찾지 못하도록 설정
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class, () -> scheduleService.getSchedule(scheduleId));
        verify(scheduleRepository, times(1)).findById(scheduleId);
    }

    // 4. 스케줄 삭제 테스트 (성공)
    @Test
    void deleteSchedule_success() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .location("Seoul")
                .build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 스케줄 조회
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // Mock 설정: 호스트 역할로 참가한 상태로 설정
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(anyLong(), eq(userId)))
                .thenReturn(Optional.of(Participant.builder()
                        .user(User.builder().userId(userId).build())
                        .meetup(meetupSchedule.getMeetup())
                        .meetupRole(ParticipantRole.HOST.name())
                        .build()));

        // when
        scheduleService.deleteSchedule(scheduleId);

        // then
        verify(scheduleRepository, times(1)).delete(meetupSchedule);
    }

    // 5. 스케줄 삭제 테스트 (실패 - 권한 없음)
    @Test
    void deleteSchedule_unauthorized() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .location("Seoul")
                .build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 스케줄 조회
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // Mock 설정: 참가자가 없는 경우
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(anyLong(), eq(userId)))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> scheduleService.deleteSchedule(scheduleId));
        verify(scheduleRepository, never()).delete(any(MeetupSchedule.class));
    }

    // 6. 스케줄 업데이트 테스트 (성공)
    @Test
    void updateSchedule_success() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;

        User user = User.builder().userId(userId).build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 호스트 역할로 참가한 상태로 설정
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(anyLong(), eq(userId)))
                .thenReturn(Optional.of(Participant.builder()
                        .user(user)
                        .meetup(Meetup.builder().meetupId(1L).build())
                        .meetupRole(ParticipantRole.HOST.name())
                        .build()));

        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .location("Seoul")
                .build();

        // 스케줄 조회 Mock 설정
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));

        // 업데이트 요청 객체 설정
        ScheduleRequest updateRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDateTime.of(2024, 11, 16, 15, 0))
                .location("Busan")
                .status("PROPOSED")
                .build();

        // 업데이트된 스케줄 Mock 설정
        when(scheduleRepository.save(existingSchedule)).thenReturn(existingSchedule);

        // when
        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, updateRequest);

        // then
        assertNotNull(response);
        assertEquals("Busan", response.getLocation());
        assertEquals(LocalDateTime.of(2024, 11, 16, 15, 0), response.getProposalDate());
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    // 7. 스케줄 업데이트 실패 테스트 (권한 없음)
    @Test
    void updateSchedule_unauthorized() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;

        User user = User.builder().userId(userId).build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 해당 사용자가 참가자가 아님
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(anyLong(), eq(userId)))
                .thenReturn(Optional.empty());

        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .location("Seoul")
                .build();

        // 스케줄 조회 Mock 설정
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));

        ScheduleRequest updateRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDateTime.of(2024, 11, 16, 15, 0))
                .location("Busan")
                .build();

        // when & then
        assertThrows(UnauthorizedException.class, () -> scheduleService.updateSchedule(scheduleId, updateRequest));
        verify(scheduleRepository, never()).save(any(MeetupSchedule.class));
    }

    // 8. 투표 시 참가자 자동 추가 테스트
    @Test
    void voteOnSchedule_addParticipantAutomatically() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;
        Meetup meetup = Meetup.builder().meetupId(1L).build();
        User user = User.builder().userId(userId).build();

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(meetup)
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 스케줄 조회
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // Mock 설정: 참가자 조회 시 참가자가 없는 상태
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetup.getMeetupId(), userId)).thenReturn(Optional.empty());

        // Mock 설정: 새로운 참가자 저장
        when(participantRepository.save(any(Participant.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        scheduleService.voteOnSchedule(scheduleId);

        // then
        verify(participantRepository, times(1)).save(any(Participant.class));
    }

    // 9. 기존 참가자 투표 테스트
    @Test
    void voteOnSchedule_existingParticipant() {
        // given
        String userId = "user1";
        Long scheduleId = 1L;
        Meetup meetup = Meetup.builder().meetupId(1L).build();
        User user = User.builder().userId(userId).build();

        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(meetup)
                .proposalDate(LocalDateTime.of(2024, 11, 15, 10, 0))
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();

        Participant participant = Participant.builder()
                .user(user)
                .meetup(meetup)
                .meetupRole(ParticipantRole.MEMBER.name())
                .status(ParticipantStatus.APPROVED)
                .hasVoted(false)
                .build();

        // Mock 설정: 인증 객체에서 사용자 ID 반환
        when(authentication.getName()).thenReturn(userId);

        // Mock 설정: 스케줄 조회
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));

        // Mock 설정: 참가자 조회 시 기존 참가자가 있는 상태
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetup.getMeetupId(), userId)).thenReturn(Optional.of(participant));

        // when
        scheduleService.voteOnSchedule(scheduleId);

        // then
        assertTrue(participant.getHasVoted());
        verify(participantRepository, times(1)).save(participant);
    }
}
