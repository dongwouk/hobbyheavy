package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParticipantService {

    private final ParticipantRepository participantRepository;
    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;

    public void createParticipant(Meetup meetup, User user,
                                  ParticipantStatus status, ParticipantRole role) {

        Participant participant = Participant.builder()
                .meetup(meetup)
                .user(user)
                .status(status.getStatus())
                .meetupRole(role.getRole())
                .meetupAlarm(true).build();

        participantRepository.save(participant);
    }

    // 승인된 모임원 조회
    public List<ParticipantApprovedResponse> getMeetupParticipants(Long meetupId) {
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);
        return participants.stream()
                .filter(participant -> ParticipantStatus.APPROVED.getStatus().equals(participant.getStatus()))
                .map(participant -> new ParticipantApprovedResponse(
                        participant.getUser().getUserId(),
                        participant.getStatus(),
                        participant.getMeetupRole()
                )).toList();
    }

    // 모임 참가
    public void joinParticipant(Long meetupId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // 모임에 사용자가 있는지 확인
        Participant participant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);
        if(participant != null) {
            if(participant.getStatus().equals(ParticipantStatus.APPROVED.getStatus()) ||
            participant.getStatus().equals(ParticipantStatus.WAITING.getStatus())) {
                throw new RuntimeException("이미 승인, 대기중인 상태는 신청할 수 없습니다.");
            }
            // 취소, 탈퇴는 대기중으로 변경
            participant.updateStatus(ParticipantStatus.WAITING);
            return;
        }
        // DB 에 저장되어 있지 않은 신청자는 새로 생성
        User user = userRepository.findByUserId(userId);
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(
                () -> new EntityNotFoundException("모임이 없습니다.")
        );
        createParticipant(meetup, user, ParticipantStatus.WAITING, ParticipantRole.MEMBER);
    }

}
