package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.entity.*;
import com.example.hobbyheavy.repository.HobbyRepository;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final HobbyRepository hobbyRepository;
    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final CommentService commentService;

    // 모임 상세 조회
    public MeetupInfoResponse infoMeetup(Long meetupId) {
        Meetup meetup = findMeetup(meetupId);
        List<Comment> comments = commentService.meetupComments(meetupId);
        List<ParticipantApprovedResponse> participants = participantService.getMeetupParticipants(meetupId);
        return new MeetupInfoResponse(meetup, comments, participants);
    }

    // 모임 생성
    public void createMeetup(MeetupCreateRequest request) {

        User user = userRepository.findByUserId(request.getHostName());

        Meetup meetup = Meetup.builder()
                .meetupName(request.getMeetupName())
                .description(request.getDescription())
                .location(request.getLocation())
                .recurrenceRule(request.getRecurrenceRule())
                .maxParticipants(request.getMaxParticipants())
                .hostUser(user)
                .build();

        // Hobby 확인 -> 서비스 미존재
        //if (request.getHobbyName() != null) {
            Hobby hobby = hobbyRepository.findFirstByHobbyName("hiking")
                    .orElseThrow(() -> new IllegalArgumentException("취미 이름이 없습니다."));
            meetup.updateHobby(hobby);
        //}

        meetupRepository.save(meetup);

        // 모임장 DB 저장
        participantService.createParticipant(meetup, user,
                ParticipantStatus.APPROVED, ParticipantRole.HOST);
    }

    @Transactional
    public void updateMeetup(Long meetupId, MeetupUpdateRequest request) throws AccessDeniedException {

        Meetup meetup = findMeetup(meetupId);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if(!meetup.getHostUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("모임의 수정 권한이 없습니다.");
        }

        meetup.updateMeetupName(request.getMeetupName());
        meetup.updateDescription(request.getDescription());
        meetup.updateLocation(request.getLocation());
        meetup.updateRecurrenceRule(request.getRecurrenceRule());
    }

    public void deleteMeetup(Long meetupId) {
        meetupRepository.deleteById(meetupId);
    }

    private Meetup findMeetup (Long meetupId) {
        return meetupRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new IllegalArgumentException("모임이 존재하지 않습니다."));
    }
}
