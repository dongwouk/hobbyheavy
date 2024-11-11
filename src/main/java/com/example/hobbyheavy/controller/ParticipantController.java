package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ParticipantStatusRequest;
import com.example.hobbyheavy.dto.response.ParticipantWaitResponse;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    @PostMapping("/{meetupId}")
    public ResponseEntity<String> WaitParticipant(@PathVariable Long meetupId) {
        participantService.joinParticipant(meetupId);
        return ResponseEntity.status(201).body("Waiting Participant Successfully.");
    }

    @PutMapping("/status")
    public ResponseEntity<String> approveParticipant(@Valid @RequestBody ParticipantStatusRequest request) {
        participantService.setStatusParticipant(request);
        return ResponseEntity.ok("Change Participant Status Successfully.");
    }

    @GetMapping("/waiting/{meetupId}")
    public ResponseEntity<List<ParticipantWaitResponse>> waitParticipants(@PathVariable Long meetupId) {
        return ResponseEntity.ok(participantService.getWaitParticipant(meetupId));
    }

}
