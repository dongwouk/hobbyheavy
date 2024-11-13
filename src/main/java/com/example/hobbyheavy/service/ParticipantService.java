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
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Part;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public void createParticipant(Meetup meetup, User user, ParticipantStatus status, ParticipantRole role) {

        Participant participant = Participant.builder().meetup(meetup).user(user).status(status).meetupRole(role.name()).meetupAlarm(true).hasVoted(false).build();

        participantRepository.save(participant);
    }

    /**
     * 승인된 모임원 조회
     **/
    public List<ParticipantApprovedResponse> getMeetupParticipants(Long meetupId) {
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);
        return participants.stream().filter(participant -> ParticipantStatus.APPROVED.equals(participant.getStatus())).map(participant -> new ParticipantApprovedResponse(participant.getUser().getUserId(), participant.getStatus(), participant.getMeetupRole(), participant.getHasVoted())).toList();
    }

    /**
     * 대기 중인 모임원 조회
     **/
    public List<ParticipantWaitResponse> getWaitParticipant(Long meetupId, String userId) {
        checkHost(meetupId, userId);
        List<Participant> participants = participantRepository.findAllByMeetup_MeetupId(meetupId);

        return participants.stream().filter(participant -> ParticipantStatus.WAITING.equals(participant.getStatus())).map(participant -> new ParticipantWaitResponse(participant.getUser())).toList();
    }

    /**
     * 모임 신청
     **/
    public void joinParticipant(Long meetupId, String userId) {
        participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId).ifPresentOrElse(participant -> {
            if (participant.getStatus() == ParticipantStatus.APPROVED || participant.getStatus() == ParticipantStatus.WAITING) {
                throw new CustomException(ExceptionCode.ALREADY_REQUEST);
            }
            participant.updateStatus(ParticipantStatus.WAITING);
            participantRepository.save(participant);
        }, () -> {
            User user = userRepository.findByUserId(userId);
            Meetup meetup = meetupRepository.findById(meetupId).orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
            createParticipant(meetup, user, ParticipantStatus.WAITING, ParticipantRole.MEMBER);
        });
    }

    @Transactional
    public void setStatusParticipant(ParticipantStatusRequest request, String userId) {
        Optional<Participant> optionalParticipant =
                participantRepository.findByMeetup_MeetupIdAndUser_UserId(request.getMeetupId(), request.getUserId());

        if (optionalParticipant.isEmpty()) {
            throw new CustomException(ExceptionCode.PARTICIPANT_NOT_FOUND);
        }

        checkHost(request.getMeetupId(), userId);
        Participant participant = optionalParticipant.get();
        participant.updateStatus(ParticipantStatus.valueOf(request.getStatus()));
    }

    private void checkHost(Long meetupId, String userId) {
        String hostId = meetupRepository.findHostNameByMeetupId(meetupId);
        if (!userId.equals(hostId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION);
        }
    }

}
