package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ParticipantStatusRequest;
import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.dto.response.ParticipantWaitResponse;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

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
                .status(status)
                .meetupRole(role.name())
                .meetupAlarm(true).build();

        participantRepository.save(participant);
    }

    // 승인된 모임원 조회
    public List<ParticipantApprovedResponse> getMeetupParticipants(Long meetupId) {
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);
        return participants.stream()
                .filter(participant -> ParticipantStatus.APPROVED.equals(participant.getStatus()))
                .map(participant -> new ParticipantApprovedResponse(
                        participant.getUser().getUserId(),
                        participant.getStatus(),
                        participant.getMeetupRole(),
                        participant.getHasVoted()
                )).toList();
    }

    // 대기 중인 모임원 조회
    public List<ParticipantWaitResponse> getWaitParticipant(Long meetupId) {

        checkHost(meetupId);

        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);
        return participants.stream()
                .filter(participant -> ParticipantStatus.WAITING.equals(participant.getStatus()))
                .map(participant -> new ParticipantWaitResponse(participant.getUser())).toList();
    }

    // 모임 신청
    public void joinParticipant(Long meetupId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        // 모임에 사용자가 있는지 확인

        Optional<Participant> optionalParticipant =
                participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);

        if (optionalParticipant.isPresent()) {
            Participant participant = optionalParticipant.get();

            // 승인 또는 대기중인 상태인 경우 예외 던짐
            if (participant.getStatus().equals(ParticipantStatus.APPROVED) ||
                    participant.getStatus().equals(ParticipantStatus.WAITING)) {
                throw new RuntimeException("이미 승인, 대기중인 상태는 신청할 수 없습니다.");
            }

            // 취소 또는 탈퇴 상태인 경우 대기중으로 변경
            participant.updateStatus(ParticipantStatus.WAITING);
            participantRepository.save(participant);
            return;
        }

        // DB 에 저장되어 있지 않은 신청자는 새로 생성
        User user = userRepository.findByUserId(userId);
        Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(
                () -> new EntityNotFoundException("모임이 없습니다.")
        );
        createParticipant(meetup, user, ParticipantStatus.WAITING, ParticipantRole.MEMBER);
    }

    @Transactional
    public void setStatusParticipant(ParticipantStatusRequest request) {

        Optional<Participant> optionalParticipant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(
                request.getMeetupId(), request.getUserId());

        if(optionalParticipant.isEmpty()) {
            throw new EntityNotFoundException("해당 신청자가 없습니다.");
        }

        checkHost(request.getMeetupId());

        Participant participant = optionalParticipant.get();
        participant.updateStatus(ParticipantStatus.valueOf(request.getStatus()));
    }

    private void checkHost(Long meetupId) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        String hostId = meetupRepository.findHostNameByMeetupId(meetupId);


        if (!userId.equals(hostId)) {
            throw new AccessDeniedException("모임장이 아닙니다.");
        }
    }

}
