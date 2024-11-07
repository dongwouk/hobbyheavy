package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.service.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{meetupId}")
    public ResponseEntity<String> WaitMeetup(@PathVariable Long meetupId) {
        participantService.joinParticipant(meetupId);
        return ResponseEntity.status(201).body("Waiting Meetup Successfully.");
    }
}
