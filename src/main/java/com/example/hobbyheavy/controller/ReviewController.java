package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ReviewCreateRequest;
import com.example.hobbyheavy.dto.request.ReviewUpdateRequest;
import com.example.hobbyheavy.dto.response.ReviewResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 리뷰(Review) 관련 API를 처리하는 컨트롤러
 */
@RestController
@RequestMapping("/api/reviews") // Review API 기본 경로
@RequiredArgsConstructor // 생성자 주입을 위한 Lombok 어노테이션
public class ReviewController {

    private final ReviewService reviewService; // 리뷰 관련 비즈니스 로직 처리

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
     * 특정 스케줄의 리뷰 목록 조회 (페이징)
     *
     * @param scheduleId 스케줄 ID
     * @param authentication Spring Security 인증 객체
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 크기 (기본값: 10)
     * @return 리뷰 목록 (페이징 처리된 데이터)
     */
    @GetMapping("/schedule/{scheduleId}")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long scheduleId, Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviews(scheduleId, getUserId(authentication), page, size));
    }

    /**
     * 특정 리뷰 조회
     *
     * @param reviewId 리뷰 ID
     * @param authentication Spring Security 인증 객체
     * @return 리뷰 정보
     */
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable Long reviewId, Authentication authentication) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId, getUserId(authentication)));
    }

    /**
     * 리뷰 생성
     *
     * @param request 리뷰 생성 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 생성 성공 메시지
     */
    @PostMapping
    public ResponseEntity<String> createReview(
            @Valid @RequestBody ReviewCreateRequest request, Authentication authentication) {
        reviewService.createReview(request, getUserId(authentication));
        return ResponseEntity.status(201).body("Create Review Successfully.");
    }

    /**
     * 리뷰 수정
     *
     * @param reviewId 리뷰 ID
     * @param request 리뷰 수정 요청 DTO
     * @param authentication Spring Security 인증 객체
     * @return 수정 성공 메시지
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        reviewService.updateReview(reviewId, request, getUserId(authentication));
        return ResponseEntity.ok("Update Review Successfully.");
    }

    /**
     * 리뷰 삭제
     *
     * @param reviewId 리뷰 ID
     * @param authentication Spring Security 인증 객체
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId, Authentication authentication) {
        reviewService.deleteReviewById(reviewId, getUserId(authentication));
        return ResponseEntity.ok("Delete Review Successfully.");
    }
}
