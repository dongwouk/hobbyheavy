package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.dto.response.MeetupListResponse;
import com.example.hobbyheavy.dto.response.ParticipantApprovedResponse;
import com.example.hobbyheavy.entity.*;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.HobbyRepository;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public List<MeetupListResponse> meetupLists() {
        return meetupRepository.findAll().stream()
                .map(MeetupListResponse::new).toList();
    }

    /**
     * 내 모임 조회
     **/
    public List<MeetupListResponse> myMeetupInfos(String userId) {
        User user = userRepository.findByUserId(userId);
        List<Participant> participants = participantRepository.findAllByUser_Id(user.getId());
        List<MeetupListResponse> myList = new ArrayList<>();
        for (Participant participant : participants) {
            if (participant.getStatus().equals(ParticipantStatus.APPROVED)) {
                myList.add(new MeetupListResponse(participant.getMeetup()));
            }
        }
        return myList;
    }

    /**
     * 모임 상세 조회
     **/
    public MeetupInfoResponse infoMeetup(Long meetupId) {
        Meetup meetup = findMeetup(meetupId);
        List<Comment> comments = commentService.meetupComments(meetupId);
        List<ParticipantApprovedResponse> participants = participantService.getMeetupParticipants(meetupId);
        return new MeetupInfoResponse(meetup, comments, participants);
    }

    /**
     * 모임 생성
     **/
    public void createMeetup(MeetupCreateRequest request, String userId) {

        User user = userRepository.findByUserId(userId);

        Meetup meetup = Meetup.builder()
                .meetupName(request.getMeetupName())
                .description(request.getDescription())
                .location(request.getLocation())
                .recurrenceRule(request.getRecurrenceRule())
                .maxParticipants(request.getMaxParticipants())
                .hostUser(user)
                .build();

        Hobby hobby = hobbyRepository.findFirstByHobbyName(request.getHobbyName())
                .orElseThrow(() -> new CustomException(ExceptionCode.HOBBY_NOT_FOUND));
        meetup.updateHobby(hobby);

        meetupRepository.save(meetup);

        participantService.createParticipant(meetup, user,
                ParticipantStatus.APPROVED, ParticipantRole.HOST);
    }

    /**
     * 모임 수정
     **/
    @Transactional
    public void updateMeetup(Long meetupId, MeetupUpdateRequest request, String userId) {

        Meetup meetup = findMeetup(meetupId);

        if (!meetup.getHostUser().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION); // 권한 없음
        }

        meetup.updateMeetupName(request.getMeetupName());
        meetup.updateDescription(request.getDescription());
        meetup.updateLocation(request.getLocation());
        meetup.updateRecurrenceRule(request.getRecurrenceRule());
        meetup.updateMaxParticipants(request.getMaxParticipants());
    }

    /**
     * 모임 삭제
     **/
    public void deleteMeetup(Long meetupId, String userId) {
        Meetup meetup = findMeetup(meetupId);

        long participantCount = 0;
        for (Participant participant : meetup.getParticipants()) {
            if (participant.getStatus().equals(ParticipantStatus.APPROVED)) {
                participantCount++;
            }
        }

        if (participantCount > 1) {
            throw new CustomException(ExceptionCode.REMAIN_PARTICIPANTS); // 참여자가 아직 있음
        }
        if (!meetup.getHostUser().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION); // 권한 없음
        }
        meetupRepository.deleteById(meetupId);
    }

    /**
     * 모임 찾기
     **/
    private Meetup findMeetup(Long meetupId) {
        return meetupRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
    }
}
