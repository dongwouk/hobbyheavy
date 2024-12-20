package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ParticipantRoleRequest;
import com.example.hobbyheavy.dto.request.ParticipantStatusRequest;
import com.example.hobbyheavy.dto.response.ParticipantWaitResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.ParticipantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/participant")
@RequiredArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;

    private String getUserId(Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
        }
        return authentication.getName();
    }

    @PostMapping("/{meetupId}")
    public ResponseEntity<String> WaitParticipant
            (@PathVariable Long meetupId, Authentication authentication) {
        participantService.joinParticipant(meetupId, getUserId(authentication));
        return ResponseEntity.status(201).body("Waiting Participant Successfully.");
    }

    @PutMapping("/waiting-cancel/{meetupId}")
    public ResponseEntity<String> WaitCancelParticipant
            (@PathVariable Long meetupId, Authentication authentication) {
        participantService.cancelWaiting(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Cancel Waiting Participant Successfully.");
    }

    @GetMapping("/waiting/{meetupId}")
    public ResponseEntity<List<ParticipantWaitResponse>> waitParticipants
            (@PathVariable Long meetupId, Authentication authentication) {
        return ResponseEntity.ok(
                participantService.getWaitParticipant(meetupId, getUserId(authentication)));
    }


    @PutMapping("/alarm/{meetupId}")
    public ResponseEntity<String> toggleMeetupAlarm(
            @PathVariable Long meetupId,
            Authentication authentication) {
        participantService.toggleMeetupAlarm(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Meetup Alarm toggled successfully.");
    }


    @PutMapping("/status")
    public ResponseEntity<String> approveParticipant
            (@Valid @RequestBody ParticipantStatusRequest request,
             Authentication authentication) {
        participantService.setHostStatus(request, getUserId(authentication));
        return ResponseEntity.ok("Change Participant Status Successfully.");
    }

    @PutMapping("/withdraw/{meetupId}")
    public ResponseEntity<String> changeParticipant
            (@PathVariable Long meetupId,
             Authentication authentication) {
        participantService.withdraw(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Withdraw Participant Successfully.");
    }

    @PutMapping("/sub-host")
    public ResponseEntity<String> changeRole (
            @Valid @RequestBody ParticipantRoleRequest request, Authentication authentication) {
        participantService.putRoleParticipant(request, getUserId(authentication));
        return ResponseEntity.ok("Member Role Change Successfully.");
    }
}
