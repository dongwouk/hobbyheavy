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

/**
 * Meetup 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/meetups") // Meetup API 기본 경로
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class MeetupController {

    private final MeetupService meetupService; // Meetup 관련 비즈니스 로직 처리

    /**
     * 인증된 사용자 ID를 가져오는 메서드
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자 ID
     */
    private String getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER); // 인증되지 않은 사용자 예외
        }
        return authentication.getName(); // 사용자 ID 반환
    }

    /**
     * 최신순으로 Meetup 리스트 반환 (페이징)
     *
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 최신순 Meetup 리스트
     */
    @GetMapping
    public ResponseEntity<Page<MeetupListResponse>> allMeetupList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "new", null));
    }

    /**
     * 취미로 Meetup 검색
     *
     * @param hobbyName 취미 이름
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 검색한 취미와 관련된 Meetup 리스트
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
     * 키워드로 Meetup 검색
     *
     * @param keyword 검색 키워드
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 키워드로 검색된 Meetup 리스트
     */
    @GetMapping("/search/{keyword}")
    public ResponseEntity<Page<MeetupListResponse>> searchMeetupList(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "search", keyword));
    }

    /**
     * 지역별 Meetup 검색
     *
     * @param loc 지역명
     * @param page 페이지 번호
     * @param size 페이지 크기
     * @return 지역에 기반한 Meetup 리스트
     */
    @GetMapping("/location/{loc}")
    public ResponseEntity<Page<MeetupListResponse>> locationMeetupList(
            @PathVariable String loc,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(meetupService.meetupLists(page, size, "location", loc));
    }

    /**
     * 특정 Meetup 정보 반환
     *
     * @param meetupId Meetup ID
     * @return Meetup 정보
     */
    @GetMapping("/{meetupId}")
    public ResponseEntity<MeetupInfoResponse> meetupInfo(@PathVariable Long meetupId) {
        return ResponseEntity.ok(meetupService.infoMeetup(meetupId));
    }

    /**
     * 사용자가 참여 중인 Meetup 리스트 반환
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자가 참여 중인 Meetup 리스트
     */
    @GetMapping("/my")
    public ResponseEntity<List<MeetupListResponse>> myMeetupInfos(Authentication authentication) {
        return ResponseEntity.ok(meetupService.myMeetupInfos(getUserId(authentication)));
    }

    /**
     * 새로운 Meetup 생성
     *
     * @param request Meetup 생성 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 생성 성공 메시지
     */
    @PostMapping
    public ResponseEntity<String> createMeetup(
            @Valid @RequestBody MeetupCreateRequest request, Authentication authentication) {
        meetupService.createMeetup(request, getUserId(authentication));
        return ResponseEntity.status(201).body("Meetups created successfully.");
    }

    /**
     * Meetup 썸네일 업로드
     *
     * @param meetupId Meetup ID
     * @param image 썸네일 이미지 파일
     * @param authentication Spring Security 인증 객체
     * @return 업로드 성공 메시지
     */
    @PutMapping("/{meetupId}/thumbnail")
    public ResponseEntity<String> uploadThumbnail(
            @PathVariable long meetupId,
            @RequestParam("image") MultipartFile image,
            Authentication authentication) {
        meetupService.uploadThumbnail(meetupId, image, getUserId(authentication));
        return ResponseEntity.ok("Meetup thumbnail successfully.");
    }

    /**
     * Meetup 정보 수정
     *
     * @param meetupId Meetup ID
     * @param meetupUpdateRequest Meetup 수정 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 수정 성공 메시지
     */
    @PutMapping("/{meetupId}")
    public ResponseEntity<String> updateMeetups(
            @PathVariable Long meetupId,
            @Valid @RequestBody MeetupUpdateRequest meetupUpdateRequest,
            Authentication authentication) {
        meetupService.updateMeetup(meetupId, meetupUpdateRequest, getUserId(authentication));
        return ResponseEntity.ok("Meetups updated successfully.");
    }

    /**
     * Meetup 삭제
     *
     * @param meetupId Meetup ID
     * @param authentication Spring Security 인증 객체
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/{meetupId}")
    public ResponseEntity<String> deleteMeetup(
            @PathVariable Long meetupId, Authentication authentication) {
        meetupService.deleteMeetup(meetupId, getUserId(authentication));
        return ResponseEntity.ok("Meetups deleted successfully.");
    }
}
