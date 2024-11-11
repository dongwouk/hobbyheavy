package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.dto.response.MeetupMyListResponse;
import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.entity.*;
import com.example.hobbyheavy.repository.HobbyRepository;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeetupService {

    private final HobbyRepository hobbyRepository;
    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final CommentService commentService;
    private final ParticipantRepository participantRepository;

    // 내 모임 조회
    public List<MeetupMyListResponse> myMeetupInfos() {
        User user = userRepository.findByUserId(SecurityContextHolder.getContext().getAuthentication().getName());
        List<Participant> participants = participantRepository.findAllByUser_Id(user.getId());
        List<MeetupMyListResponse> myList = new ArrayList<>();
        for (Participant participant : participants) {
            if (participant.getStatus().equals(ParticipantStatus.APPROVED)) {
                myList.add(new MeetupMyListResponse(participant.getMeetup(), participant.getMeetup().getHobby()));
            }
        }
        return myList;
    }

    // 모임 상세 조회
    public MeetupInfoResponse infoMeetup(Long meetupId) {
        Meetup meetup = findMeetup(meetupId);
        List<Comment> comments = commentService.meetupComments(meetupId);
        List<ParticipantApprovedResponse> participants = participantService.getMeetupParticipants(meetupId);
        return new MeetupInfoResponse(meetup, comments, participants);
    }

    // 모임 생성
    public void createMeetup(MeetupCreateRequest request) {

        User user = userRepository.findByUserId(SecurityContextHolder.getContext().getAuthentication().getName());

        Meetup meetup = Meetup.builder()
                .meetupName(request.getMeetupName())
                .description(request.getDescription())
                .location(request.getLocation())
                .recurrenceRule(request.getRecurrenceRule())
                .maxParticipants(request.getMaxParticipants())
                .hostUser(user)
                .build();

        Hobby hobby = hobbyRepository.findFirstByHobbyName(request.getHobbyName())
                .orElseThrow(() -> new IllegalArgumentException("취미 이름이 없습니다."));
        meetup.updateHobby(hobby);

        meetupRepository.save(meetup);

        // 모임장 DB 저장
        participantService.createParticipant(meetup, user,
                ParticipantStatus.APPROVED, ParticipantRole.HOST);
    }

    @Transactional
    public void updateMeetup(Long meetupId, MeetupUpdateRequest request) throws AccessDeniedException {

        Meetup meetup = findMeetup(meetupId);
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        if (!meetup.getHostUser().getUserId().equals(userId)) {
            throw new AccessDeniedException("모임의 수정 권한이 없습니다.");
        }

        meetup.updateMeetupName(request.getMeetupName());
        meetup.updateDescription(request.getDescription());
        meetup.updateLocation(request.getLocation());
        meetup.updateRecurrenceRule(request.getRecurrenceRule());
        meetup.updateMaxParticipants(request.getMaxParticipants());
    }

    public void deleteMeetup(Long meetupId) {
        Meetup meetup = findMeetup(meetupId);

        long participantCount = 0;
        for (Participant participant : meetup.getParticipants()) {
            if (participant.getStatus().equals(ParticipantStatus.APPROVED)) {
                participantCount++;
            }
        }

        if (participantCount > 1) {
            // 모임 인원이 있으면  에러 생성
            throw new IllegalStateException("모임에 아직 남아 있는 인원이 있습니다.");
        }

        if (!meetup.getHostUser().getUserId().equals(SecurityContextHolder.getContext().getAuthentication().getName())) {
            // 삭제 권한이 없음
            System.out.println("모임장이 아님");
            return;
        }

        meetupRepository.deleteById(meetupId);
    }

    private Meetup findMeetup(Long meetupId) {
        return meetupRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new IllegalArgumentException("모임이 존재하지 않습니다."));
    }
}
