package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.CommentResponse;
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
import com.example.hobbyheavy.util.ImageUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetupService {

    private final HobbyRepository hobbyRepository;
    private final MeetupRepository meetupRepository;
    private final UserRepository userRepository;
    private final ParticipantService participantService;
    private final CommentService commentService;
    private final ParticipantRepository participantRepository;

    /**
     * 모임 검색 (최신순, 취미별, 검색키워드, 위치)
     * @param page
     * @param size
     * @return List<MeetupListResponse>
     */
    public Page<MeetupListResponse> meetupLists(int page, int size, String keyword, String value) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Meetup> meetupPage = switch (keyword) {
            case "new" ->
                    meetupRepository.findAllByOrderByCreatedDateDesc(pageable);
            case "hobby" ->
                    getHobbyList(value, pageable);
            case "search" ->
                    meetupRepository.findAllByMeetupNameContainingOrDescriptionContaining(pageable, value, value);
            case "location" ->
                    meetupRepository.findAllByLocationContaining(pageable, value);
            default -> throw new CustomException(ExceptionCode.INVALID_SEARCH_KEYWORD);
        };
        return meetupPage.map(MeetupListResponse::new);
    }

    private Page<Meetup> getHobbyList (String keyword, Pageable pageable) {
        if (!hobbyRepository.existsByHobbyName(keyword)) {
            throw new CustomException(ExceptionCode.HOBBY_NOT_FOUND);
        }
        return meetupRepository.findAllByHobby_HobbyNameOrderByCreatedDateDesc(pageable, keyword);
    }

    /**
     * 내 모임 조회
     **/
    public List<MeetupListResponse> myMeetupInfos(String userId) {
        User user = getUser(userId);
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
        Meetup meetup = meetupRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
        List<CommentResponse> comments = commentService.meetupComments(meetupId);
        List<ParticipantApprovedResponse> participants = participantService.getMeetupParticipants(meetupId);
        return new MeetupInfoResponse(meetup, comments, participants);
    }

    /**
     * 모임 생성
     **/
    public void createMeetup(MeetupCreateRequest request, String userId) {

        User user = getUser(userId);

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
     * 썸네일 등록
     * @param meetupId
     * @param image
     */
    @Transactional
    public void uploadThumbnail(Long meetupId, MultipartFile image, String userId){
        Meetup meetup = findMeetup(meetupId, userId);

        // 이미지 파일 저장을 위한 경로 설정
        String uploadsDir = "src/main/resources/static/uploads/thumbnails/";
        String filePath = uploadsDir + meetup.getThumbnail();

        File file = new File(filePath);
        boolean result = file.delete();
        log.info("파일 이름 : {}, 삭제 결과 : {}", filePath, result);

        if (image.getSize() == 0) {
            meetup.updateThumbnail("");
            return;
        }

        // 이미지 파일 경로를 저장
        String dbFilePath = ImageUtil.saveImage(image, uploadsDir);
        meetup.updateThumbnail(dbFilePath);
    }

    /**
     * 모임 수정
     **/
    @Transactional
    public void updateMeetup(Long meetupId, MeetupUpdateRequest request, String userId) {
        Meetup meetup = findMeetup(meetupId, userId);
        try {
            meetup.updateMeetupName(request.getMeetupName());
            meetup.updateDescription(request.getDescription());
            meetup.updateLocation(request.getLocation());
            meetup.updateRecurrenceRule(request.getRecurrenceRule());
            meetup.updateMaxParticipants(request.getMaxParticipants());
        } catch (Exception e) {
            log.error("모임 아이디 : {}, 모임 수정에 실패했습니다. 에러 메세지 : {}", meetupId, e.getMessage());
            throw new CustomException(ExceptionCode.MEETUP_UPDATE_FAILED);
        }
    }

    /**
     * 모임 삭제
     **/
    public void deleteMeetup(Long meetupId, String userId) {
        Meetup meetup = findMeetup(meetupId, userId);

        long participantCount = 0;
        for (Participant participant : meetup.getParticipants()) {
            if (participant.getStatus().equals(ParticipantStatus.APPROVED)) {
                participantCount++;
            }
        }

        if (participantCount > 1) {
            throw new CustomException(ExceptionCode.REMAIN_PARTICIPANTS); // 참여자가 아직 있음
        }

        try{
            meetup.markAsDeleted();
            meetupRepository.save(meetup);
        } catch (Exception e) {
            log.error("MeetupId : {} 모임 삭제에 실패했습니다. - {}", meetupId, e.getMessage());
            throw new CustomException(ExceptionCode.MEETUP_DELETE_FAILED);
        }

    }

    /**
     * 모임 찾기
     **/
    private Meetup findMeetup(Long meetupId, String userId) {
        Meetup meetup = meetupRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));

        if (!meetup.getHostUser().getUserId().equals(userId)) {
            throw new CustomException(ExceptionCode.FORBIDDEN_ACTION); // 권한 없음
        }
        return meetup;
    }

    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }
}
