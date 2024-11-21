package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@SpringBootTest
public class VoteServiceTest {

    @Autowired
    private VoteService voteService;

    @MockBean
    private ScheduleRepository scheduleRepository;

    @MockBean
    private ParticipantRepository participantRepository;

    private MeetupSchedule meetupSchedule;
    private User user;

    @BeforeEach
    public void setUp() {
        // Mock MeetupSchedule 객체 초기화
        Meetup meetup = Meetup.builder().meetupId(1L).build();
        meetupSchedule = MeetupSchedule.builder()
                .scheduleId(1L)
                .meetup(meetup)
                .build();

        // Mock User 객체 초기화
        user = User.builder().userId("testUser").build();
    }

    @Test
    public void testVoteOnSchedule_NewParticipant() {
        // 스케줄이 존재하도록 설정
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));
        // 참가자가 없는 경우
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(1L, "testUser")).thenReturn(Optional.empty());

        // VoteService의 voteOnSchedule() 메서드 호출
        voteService.voteOnSchedule(1L, "testUser");

        // 참가자가 저장되었는지 확인
        Mockito.verify(participantRepository).save(any(Participant.class));
    }

    @Test
    public void testVoteOnSchedule_ExistingParticipant_NotVoted() {
        // 스케줄이 존재하도록 설정
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));
        // 기존 참가자가 있고 아직 투표하지 않은 경우
        Participant participant = Participant.builder()
                .user(user)
                .meetup(meetupSchedule.getMeetup())
                .meetupRole(ParticipantRole.MEMBER.name())
                .status(ParticipantStatus.APPROVED)
                .hasVoted(false)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(1L, "testUser")).thenReturn(Optional.of(participant));

        // VoteService의 voteOnSchedule() 메서드 호출
        voteService.voteOnSchedule(1L, "testUser");

        // 참가자가 투표 완료 상태로 업데이트되었는지 확인
        assertThat(participant.getHasVoted()).isTrue();
        Mockito.verify(participantRepository).save(participant);
    }

    @Test
    public void testVoteOnSchedule_AlreadyVoted() {
        // 스케줄이 존재하도록 설정
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.of(meetupSchedule));
        // 기존 참가자가 있고 이미 투표한 경우
        Participant participant = Participant.builder()
                .user(user)
                .meetup(meetupSchedule.getMeetup())
                .meetupRole(ParticipantRole.MEMBER.name())
                .status(ParticipantStatus.APPROVED)
                .hasVoted(true)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(1L, "testUser")).thenReturn(Optional.of(participant));

        // 이미 투표한 경우 예외가 발생하는지 확인
        assertThatThrownBy(() -> voteService.voteOnSchedule(1L, "testUser"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ExceptionCode.ALREADY_VOTED.getMessage());
    }

    @Test
    public void testVoteOnSchedule_ScheduleNotFound() {
        // 스케줄이 존재하지 않는 경우
        Mockito.when(scheduleRepository.findById(1L)).thenReturn(Optional.empty());

        // 스케줄이 없을 때 예외가 발생하는지 확인
        assertThatThrownBy(() -> voteService.voteOnSchedule(1L, "testUser"))
                .isInstanceOf(CustomException.class)
                .hasMessage(ExceptionCode.SCHEDULE_NOT_FOUND.getMessage());
    }

    @Test
    public void testCheckVotingStatus() {
        // 참가자가 있고 투표한 경우
        Participant participant = Participant.builder()
                .user(user)
                .meetup(meetupSchedule.getMeetup())
                .hasVoted(true)
                .build();
        Mockito.when(participantRepository.findByMeetup_MeetupIdAndUser_UserId(1L, "testUser")).thenReturn(Optional.of(participant));

        // VoteService의 checkVotingStatus() 메서드 호출
        boolean hasVoted = voteService.checkVotingStatus(1L, "testUser");

        // 투표 상태가 true인지 확인
        assertThat(hasVoted).isTrue();
    }

    @Test
    public void testGetVoteResults() {
        // 투표 완료한 참가자 수를 반환하도록 설정
        Mockito.when(participantRepository.countByMeetup_MeetupIdAndHasVotedTrue(1L)).thenReturn(5L);

        // VoteService의 getVoteResults() 메서드 호출
        long voteCount = voteService.getVoteResults(1L);

        // 투표 완료한 참가자 수가 5인지 확인
        assertThat(voteCount).isEqualTo(5L);
    }
}
