package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.dto.response.MeetupInfoResponse;
import com.example.hobbyheavy.service.MeetupsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/meetup")
@RequiredArgsConstructor
public class MeetupController {

    private final MeetupsService meetupsService;

    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupInfoResponse> meetupInfo(@PathVariable Long meetupId) {
        return ResponseEntity.ok(meetupsService.infoMeetup(meetupId));
    }

    @PostMapping
    public ResponseEntity<String> createMeetup(@Valid @RequestBody MeetupCreateRequest request){
        meetupsService.createMeetup(request);
        return ResponseEntity.status(201).body("Meetups created successfully.");
    }

    @PutMapping("/{meetupId}")
    public ResponseEntity<String> updateMeetups(@PathVariable Long meetupId, @Valid @RequestBody MeetupUpdateRequest meetupUpdateRequest) throws AccessDeniedException {
        meetupsService.updateMeetup(meetupId, meetupUpdateRequest);
        return ResponseEntity.ok("Meetups updated successfully.");
    }

    @DeleteMapping("/{meetupId}")
    public ResponseEntity<String> deleteMeetup(@PathVariable Long meetupId){
        meetupsService.deleteMeetup(meetupId);
        return ResponseEntity.ok("Meetups deleted successfully.");
    }
}