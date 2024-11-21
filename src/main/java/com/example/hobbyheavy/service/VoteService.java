package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 스케줄 투표와 관련된 비즈니스 로직을 처리하는 서비스 클래스.
 */
@Service
@RequiredArgsConstructor
public class VoteService {

    private final ScheduleRepository scheduleRepository;
    private final ParticipantRepository participantRepository;

    /**
     * 특정 스케줄에 대해 사용자가 투표를 수행합니다.
     * <p>
     * 사용자가 기존 참가자가 아닌 경우 자동으로 참가자로 추가되고,
     * 투표 완료 상태로 설정됩니다.
     * </p>
     *
     * @param scheduleId    투표할 스케줄의 ID
     * @param userId        투표하는 사용자의 ID
     * @throws CustomException 해당 ID의 스케줄이 존재하지 않을 경우 발생
     * @throws RuntimeException 사용자가 이미 투표를 완료한 경우 발생
     */
    @Transactional
    public void voteOnSchedule(Long scheduleId, String userId) {

        MeetupSchedule meetupSchedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));

        // 참가자 조회
        Optional<Participant> optionalParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupSchedule.getMeetup().getMeetupId(), userId);
        Participant participant;

        if (optionalParticipant.isEmpty()) {
            // 참가자가 없으면 자동으로 추가하고 투표 상태도 함께 설정
            User user = User.builder().userId(userId).build(); // 사용자를 생성하거나 조회하는 로직 필요
            participant = Participant.builder()
                    .user(user)
                    .meetup(meetupSchedule.getMeetup())
                    .meetupRole(ParticipantRole.MEMBER.name()) // 일반 참가자로 설정
                    .status(ParticipantStatus.APPROVED) // 기본 상태 설정
                    .hasVoted(true) // 투표 완료 상태로 설정
                    .build();

            participantRepository.save(participant);
        } else {
            // 기존 참가자라면 해당 객체 사용
            participant = optionalParticipant.get();

            // 이미 투표했는지 확인
            if (participant.getHasVoted()) {
                throw new CustomException(ExceptionCode.ALREADY_VOTED);
            }

            // 투표 완료 처리
            participant.setHasVoted(true);
            participantRepository.save(participant);
        }
    }

    /**
     * 특정 사용자가 스케줄에 대해 투표를 완료했는지 확인합니다.
     *
     * @param scheduleId 투표를 확인할 스케줄의 ID
     * @param userId     확인할 사용자의 ID
     * @return 사용자가 해당 스케줄에 대해 투표를 완료했으면 true, 아니면 false
     */
    public boolean checkVotingStatus(Long scheduleId, String userId) {
        Optional<Participant> participantOptional = participantRepository.findByMeetup_MeetupIdAndUser_UserId(scheduleId, userId);
        return participantOptional.map(Participant::getHasVoted).orElse(false);
    }

    /**
     * 특정 스케줄에 대한 투표 완료 수를 조회합니다.
     *
     * @param scheduleId 투표 결과를 확인할 스케줄의 ID
     * @return 투표를 완료한 참가자의 수
     */
    public long getVoteResults(Long scheduleId) {
        return participantRepository.countByMeetup_MeetupIdAndHasVotedTrue(scheduleId);
    }
}
