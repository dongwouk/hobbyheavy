package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.dto.response.ScheduleResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.exception.ScheduleNotFoundException;
import com.example.hobbyheavy.exception.UnauthorizedException;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import com.example.hobbyheavy.type.ParticipantRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

    @InjectMocks
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
    }

    // 테스트: 모임 스케줄 생성 성공 테스트
    @Test
    void createSchedule_success() {
        // given
        String userId = "user1";
        when(authentication.getName()).thenReturn(userId);
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDate.now().atStartOfDay())  // proposalDate 추가
                .status("PROPOSED")
                .build();
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDate.now().atStartOfDay()) // proposalDate 추가
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();
        when(scheduleRepository.save(any(MeetupSchedule.class))).thenReturn(meetupSchedule);
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(scheduleRequest.getMeetupId(), userId))
                .thenReturn(Optional.of(Participant.builder().meetupRole(ParticipantRole.HOST.name()).build()));

        // when
        ScheduleResponse response = scheduleService.createSchedule(scheduleRequest);

        // then
        assertNotNull(response);
        assertEquals(1L, response.getScheduleId()); // 추가 검증: scheduleId 확인
        verify(scheduleRepository, times(1)).save(any(MeetupSchedule.class));
    }

    // 테스트: 스케줄 조회 실패 (존재하지 않는 스케줄)
    @Test
    void getSchedule_notFound() {
        // given
        Long scheduleId = 1L;
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ScheduleNotFoundException.class, () -> {
            scheduleService.getSchedule(scheduleId);
        });
    }

    // 테스트: 모임 스케줄 수정 성공 테스트
    @Test
    void updateSchedule_success() {
        // given
        Long scheduleId = 1L;
        String userId = "user1";
        when(authentication.getName()).thenReturn(userId);
        ScheduleRequest scheduleRequest = ScheduleRequest.builder()
                .meetupId(1L)
                .proposalDate(LocalDate.now().atStartOfDay())  // proposalDate 추가
                .activateTime(LocalDate.now().plusDays(1).atStartOfDay())  // activateTime 추가
                .status("PROPOSED")
                .participant("participant1")
                .votes(10)
                .location("Seoul")
                .votingDeadline(LocalDate.now().plusDays(5))  // votingDeadline 추가
                .build();
        MeetupSchedule existingSchedule = MeetupSchedule.builder()
                .scheduleId(scheduleId)
                .meetup(Meetup.builder().meetupId(1L).build())
                .proposalDate(LocalDate.now().atStartOfDay()) // proposalDate 추가
                .activateTime(LocalDate.now().plusDays(1).atStartOfDay())  // activateTime 추가
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .participant("participant1")
                .votes(10)
                .location("Seoul")
                .votingDeadline(LocalDate.now().plusDays(5))  // votingDeadline 추가
                .build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(existingSchedule));
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(scheduleRequest.getMeetupId(), userId))
                .thenReturn(Optional.of(Participant.builder().meetupRole(ParticipantRole.HOST.name()).build()));

        // when
        ScheduleResponse response = scheduleService.updateSchedule(scheduleId, scheduleRequest);

        // then
        assertNotNull(response);
        assertEquals(scheduleId, response.getScheduleId()); // 추가 검증: scheduleId 확인
        verify(scheduleRepository, times(1)).save(existingSchedule);
    }

    // 테스트: 권한 없는 사용자가 스케줄 삭제 시도 시 실패
    @Test
    void deleteSchedule_unauthorized() {
        // given
        Long scheduleId = 1L;
        String userId = "user1";
        when(authentication.getName()).thenReturn(userId);
        MeetupSchedule meetupSchedule = MeetupSchedule.builder().scheduleId(scheduleId).meetup(Meetup.builder().meetupId(1L).build()).build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(UnauthorizedException.class, () -> {
            scheduleService.deleteSchedule(scheduleId);
        });
    }

    // 테스트: 이미 투표한 사용자가 다시 투표 시도 시 실패
    @Test
    void voteOnSchedule_alreadyVoted() {
        // given
        Long scheduleId = 1L;
        String userId = "user1";
        when(authentication.getName()).thenReturn(userId);
        MeetupSchedule meetupSchedule = MeetupSchedule.builder().scheduleId(scheduleId).meetup(Meetup.builder().meetupId(1L).build()).build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));
        Participant participant = Participant.builder().hasVoted(true).build();
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId))
                .thenReturn(Optional.of(participant));

        // when & then
        assertThrows(RuntimeException.class, () -> {
            scheduleService.voteOnSchedule(scheduleId);
        });
    }

    // 테스트: 투표 마감일이 지난 스케줄 확정 테스트
    @Test
    void finalizeSchedules_confirmed() {
        // given
        MeetupSchedule meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .votingDeadline(LocalDate.now().minusDays(1))
                .scheduleStatus(MeetupScheduleStatus.PROPOSED)
                .build();
        when(scheduleRepository.findAll()).thenReturn(List.of(meetupSchedule));

        // when
        scheduleService.finalizeSchedules();

        // then
        assertEquals(MeetupScheduleStatus.CONFIRMED, meetupSchedule.getScheduleStatus());
        verify(scheduleRepository, times(1)).save(meetupSchedule);
    }

    // 테스트: 일정 취소 성공 테스트
    @Test
    void cancelSchedule_success() {
        // given
        Long scheduleId = 1L;
        String userId = "user1";
        when(authentication.getName()).thenReturn(userId);
        MeetupSchedule meetupSchedule = MeetupSchedule.builder().scheduleId(scheduleId).meetup(Meetup.builder().meetupId(1L).build()).build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(meetupSchedule));
        when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId))
                .thenReturn(Optional.of(Participant.builder().meetupRole(ParticipantRole.HOST.name()).build()));

        // when
        scheduleService.cancelSchedule(scheduleId, "개인 사정으로 취소");

        // then
        assertEquals(MeetupScheduleStatus.CANCELLED, meetupSchedule.getScheduleStatus());
        verify(scheduleRepository, times(1)).save(meetupSchedule);
    }
}
