package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.ReviewCreateRequest;
import com.example.hobbyheavy.dto.request.ReviewUpdateRequest;
import com.example.hobbyheavy.dto.response.ReviewResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    private String getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
        }
        return authentication.getName();
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<Page<ReviewResponse>> getReviews(
            @PathVariable Long scheduleId, Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(reviewService.getReviews(scheduleId, getUserId(authentication), page, size));
    }

    @GetMapping("/info/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable Long reviewId, Authentication authentication) {
        return ResponseEntity.ok(reviewService.getReviewById(reviewId, getUserId(authentication)));
    }

    @PostMapping("/create")
    public ResponseEntity<String> createReview(
            @RequestBody ReviewCreateRequest request, Authentication authentication) {
        reviewService.createReview(request, getUserId(authentication));
        return ResponseEntity.ok("Create Review Successfully.");
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<String> updateReview(
            @PathVariable Long reviewId, @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        reviewService.updateReview(reviewId, request, getUserId(authentication));
        return ResponseEntity.ok("Update Review Successfully.");
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<String> deleteReview(
            @PathVariable Long reviewId, Authentication authentication) {
        reviewService.deleteReviewById(reviewId, getUserId(authentication));
        return ResponseEntity.ok("Delete Review Successfully.");
    }
}
