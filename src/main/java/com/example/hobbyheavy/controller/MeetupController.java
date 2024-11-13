package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.dto.response.MeetupListResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.MeetupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/meetup")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupService meetupService;

    private String getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
        }
        return authentication.getName();
    }

    @GetMapping
    public ResponseEntity<List<MeetupListResponse>> allMeetupList() {
        return ResponseEntity.ok(meetupService.meetupLists());
    }

    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupInfoResponse> meetupInfo(@PathVariable Long meetupId) {
        return ResponseEntity.ok(meetupService.infoMeetup(meetupId));
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<MeetupListResponse>> myMeetupInfos(Authentication authentication) {
        return ResponseEntity.ok(meetupService.myMeetupInfos(getUserId(authentication)));
    }

    @PostMapping
    public ResponseEntity<String> createMeetup
            (@Valid @RequestBody MeetupCreateRequest request, Authentication authentication) {
        meetupService.createMeetup(request, getUserId(authentication));
        return ResponseEntity.status(201).body("Meetups created successfully.");
    }

    @PutMapping("/{meetupId}")
    public ResponseEntity<String> updateMeetups
            (@PathVariable Long meetupId, @Valid @RequestBody MeetupUpdateRequest meetupUpdateRequest
                    , Authentication authentication) {
        meetupService.updateMeetup(meetupId, meetupUpdateRequest, getUserId(authentication));
        return ResponseEntity.ok("Meetups updated successfully.");
    }

    @DeleteMapping("/{meetupId}")
    public ResponseEntity<String> deleteMeetup
            (@PathVariable Long meetupId, Authentication authentication) {
        meetupService.deleteMeetup(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Meetups deleted successfully.");
    }
}