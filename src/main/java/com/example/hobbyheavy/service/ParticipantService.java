package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ParticipantStatusRequest;
import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.dto.response.ParticipantWaitResponse;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
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

    /**
     * 승인된 모임원 조회
     **/
    public List<ParticipantApprovedResponse> getMeetupParticipants(Long meetupId) {
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);
        return participants.stream()
                .filter(participant -> ParticipantStatus.APPROVED.equals(participant.getStatus()))
                .map(participant -> new ParticipantApprovedResponse(
                        participant.getUser().getUserId(),
                        participant.getStatus(),
                        participant.getMeetupRole()
                        )).toList();
    }

    @Transactional
    public void cancelWaiting (Long meetupId, String userId) {
        Participant participant = getParticipant(meetupId, userId);
        if (participant.getStatus() != ParticipantStatus.WAITING) {
            log.error("요청한 참여자 : {}, 요청자 상태 : {}", userId, participant.getStatus());
            throw new CustomException(ExceptionCode.NOT_STATUS_WAITING);
        }
        participant.updateStatus(ParticipantStatus.WITHDRAWN);

    }

    /**
     * 대기 중인 모임원 조회
     **/
    public List<ParticipantWaitResponse> getWaitParticipant(Long meetupId, String userId) {
        checkHost(meetupId, userId);
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);

        return participants.stream()
                .filter(participant -> ParticipantStatus.WAITING.equals(participant.getStatus()))
                .map(participant -> new ParticipantWaitResponse(participant.getUser())).toList();
    }

    /**
     * 모임 신청
     **/
    public void joinParticipant(Long meetupId, String userId) {
        participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId)
                .ifPresentOrElse(participant -> {
                    if (participant.getStatus() == ParticipantStatus.APPROVED ||
                            participant.getStatus() == ParticipantStatus.WAITING) {
                        throw new CustomException(ExceptionCode.ALREADY_REQUEST);
                    }
                    participant.updateStatus(ParticipantStatus.WAITING);
                    participantRepository.save(participant);
                }, () -> {
                    User user = getUser(userId);
                    Meetup meetup = meetupRepository.findById(meetupId)
                            .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
                    createParticipant(meetup, user, ParticipantStatus.WAITING, ParticipantRole.MEMBER);
                });
    }

    @Transactional
    public void setHostStatusParticipant(ParticipantStatusRequest request, String userId) {
        checkHost(request.getMeetupId(), userId);
        Participant participant = getParticipant(request.getMeetupId(), userId);
        participant.updateStatus(ParticipantStatus.valueOf(request.getStatus()));
    }

    @Transactional
    public void withdrawParticipant(ParticipantStatusRequest request, String userId) {
        Participant participant = getParticipant(request.getMeetupId(), userId);
        if (participant.getStatus() != ParticipantStatus.APPROVED) {
            log.error("유저의 상태 : {}, 탈퇴를 요청자가 승인 상태가 아닙니다.", participant.getStatus());
            throw new CustomException(ExceptionCode.NOT_STATUS_APPROVED);
        }
        participant.updateStatus(ParticipantStatus.WITHDRAWN);
    }

    private Participant getParticipant(Long meetupId, String userId) {
        Optional<Participant> optionalParticipant =
                participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId);

        if (optionalParticipant.isEmpty()) {
            throw new CustomException(ExceptionCode.PARTICIPANT_NOT_FOUND);
        }
        return optionalParticipant.get();
    }

    private void checkHost(Long meetupId, String userId) {
        String hostId = meetupRepository.findHostNameByMeetupId(meetupId);
        if (!userId.equals(hostId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }
    }

    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }


    public void toggleMeetupAlarm(Long meetupId, String userId) {
        // 모임의 참가자 정보를 조회
        Participant participant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PARTICIPANT_NOT_FOUND));

        // 현재 알람 상태를 반대로 변경
        participant.updateMeetupAlarm();

        // 변경된 상태를 저장
        participantRepository.save(participant);
        log.info("Meetup alarm status toggled for user: {}, meetup: {}, new alarm status: {}", userId, meetupId, participant.getMeetupAlarm());
    }
}
