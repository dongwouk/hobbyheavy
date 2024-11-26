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
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    /**
     * 최신순 (페이징)
     * @param page
     * @param size
     * meetup?page={페이지}&size={사이즈}
     * @return 모임 리스트 최신순
     */
    @GetMapping
    public ResponseEntity<Page<MeetupListResponse>> allMeetupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "new", null));
    }

    /**
     * 취미 검색
     * @param hobbyName
     * @param page
     * @param size
     * @return 검색한 취미의 모임들
     */
    @GetMapping("/hobby/{hobbyName}")
    public ResponseEntity<Page<MeetupListResponse>> hobbyMeetupList(
            @PathVariable String hobbyName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "hobby", hobbyName));
    }

    /**
     * 키워드 검색
     * @param keyword
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<MeetupListResponse>> searchMeetupList(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "search", keyword));
    }

    @GetMapping("/location/{loc}")
    public ResponseEntity<Page<MeetupListResponse>> locationMeetupList(
            @PathVariable String loc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "location", loc));
    }

    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupInfoResponse> meetupInfo(@PathVariable Long meetupId) {
        return ResponseEntity.ok(meetupService.infoMeetup(meetupId));
    }

    @GetMapping("/my-list")
    public ResponseEntity<List<MeetupListResponse>> myMeetupInfos(Authentication authentication) {
        return ResponseEntity.ok(meetupService.myMeetupInfos(getUserId(authentication)));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createMeetup
            (@Valid @RequestBody MeetupCreateRequest request, Authentication authentication) {
        meetupService.createMeetup(request, getUserId(authentication));
        return ResponseEntity.status(201).body("Meetups created successfully.");
    }

    @PutMapping("/thumbnail/{meetupId}")
    public ResponseEntity<String> uploadThumbnail(
            @PathVariable long meetupId,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        meetupService.uploadThumbnail(meetupId, image, getUserId(authentication));
        return ResponseEntity.ok("Meetup thumbnail successfully.");
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