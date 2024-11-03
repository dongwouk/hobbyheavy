package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.MeetupCreateRequest;
import com.example.hobbyheavy.dto.request.MeetupUpdateRequest;
import com.example.hobbyheavy.service.MeetupsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;

@RestController
@RequestMapping("/meetups")
@RequiredArgsConstructor
public class MeetupsController {

    private final MeetupsService meetupsService;

    @GetMapping("/{meetupId}")
    public ResponseEntity<?> meetupInfo(@PathVariable Long meetupId) {
        return ResponseEntity.ok(meetupsService.infoMeetup(meetupId));
    }

    @PostMapping
    public ResponseEntity<?> createMeetup(@Valid @RequestBody MeetupCreateRequest request){
        meetupsService.createMeetup(request);
        return ResponseEntity.ok("Meetups created successfully.");
    }

    @PutMapping("/{meetupId}")
    public ResponseEntity<?> updateMeetups(@PathVariable Long meetupId, @Valid @RequestBody MeetupUpdateRequest meetupUpdateRequest) throws AccessDeniedException {
        meetupsService.updateMeetup(meetupId, meetupUpdateRequest);
        return ResponseEntity.ok("Meetups updated successfully.");
    }

    @DeleteMapping("/{meetupId}")
    public ResponseEntity<?> deleteMeetup(@PathVariable Long meetupId){
        meetupsService.deleteMeetup(meetupId);
        return ResponseEntity.ok("Meetups deleted successfully.");
    }
}