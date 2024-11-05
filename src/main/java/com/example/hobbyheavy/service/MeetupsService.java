package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.entity.Hobby;
import com.example.hobbyheavy.entity.Meetups;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.HobbyRepository;
import com.example.hobbyheavy.repository.MeetupsRepository;
import com.example.hobbyheavy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class MeetupsService {

    private final HobbyRepository hobbyRepository;
    private final MeetupsRepository meetupsRepository;
    private final UserRepository userRepository;

    public MeetupInfoResponse infoMeetup(Long meetupId) {
        Meetups meetups = findMeetup(meetupId);
        return new MeetupInfoResponse(meetups);
    }

    public void createMeetup(MeetupCreateRequest request) {

        if (meetupsRepository.existsByMeetupName(request.getMeetupName())) {
            throw new IllegalArgumentException("중복된 모임명입니다.");
        }

        User user = userRepository.findByUserId(request.getHostName());

        Meetups meetups = Meetups.builder()
                .meetupName(request.getMeetupName())
                .description(request.getDescription())
                .location(request.getLocation())
                .recurrenceRule(request.getRecurrenceRule())
                .maxParticipants(request.getMaxParticipants())
                .userId(user)
                .build();

        if (request.getHobbyName() != null) {
            Hobby hobby = hobbyRepository.findFirstByHobbyName(request.getHobbyName())
                    .orElseThrow(() -> new IllegalArgumentException("취미 이름이 없습니다."));
            meetups.updateHobby(hobby);
        }

        meetupsRepository.save(meetups);
    }

    @Transactional
    public void updateMeetup(Long meetupId, MeetupUpdateRequest request) throws AccessDeniedException {

        Meetups meetups = findMeetup(meetupId);

        if(meetups.getUserId().equals(SecurityContextHolder.getContext().getAuthentication().getName())){
            throw new AccessDeniedException("모임의 수정 권한이 없습니다.");
        }

        meetups.updateMeetupName(request.getMeetupName());
        meetups.updateDescription(request.getDescription());
        meetups.updateLocation(request.getLocation());
        meetups.updateRecurrenceRule(request.getRecurrenceRule());
    }

    public void deleteMeetup(Long meetupId) {
        meetupsRepository.deleteMeetupsByMeetupId(meetupId);
    }

    private Meetups findMeetup (Long meetupId) {
        return meetupsRepository.findFirstByMeetupId(meetupId)
                .orElseThrow(() -> new IllegalArgumentException("모임이 존재하지 않습니다."));
    }
}
