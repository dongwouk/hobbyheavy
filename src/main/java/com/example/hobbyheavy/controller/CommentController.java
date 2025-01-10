package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.CommentChangeRequest;
import com.example.hobbyheavy.dto.request.CommentCreateRequest;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * REST 컨트롤러: 댓글(Comment) 관련 API를 처리
 */
@RestController
@RequestMapping("/api/comments") // 댓글 관련 API 엔드포인트
@RequiredArgsConstructor // 의존성 주입을 위한 Lombok 어노테이션
public class CommentController {

    // 비즈니스 로직을 처리하는 서비스 클래스
    private final CommentService commentService;

    /**
     * 인증(Authentication) 객체에서 사용자 ID를 추출.
     * 인증되지 않은 경우 예외 발생.
     *
     * @param authentication Spring Security 인증 객체
     * @return 사용자 ID
     */
    private String getUserId(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            // 인증되지 않은 경우 커스텀 예외 발생
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
        }
        return authentication.getName(); // 인증된 사용자의 ID 반환
    }

    /**
     * 댓글 생성 API
     *
     * @param commentRequest 생성 요청 DTO
     * @param authentication 인증 객체
     * @return 생성 성공 메시지
     */
    @PostMapping
    public ResponseEntity<String> createComment(
            @Valid @RequestBody CommentCreateRequest commentRequest, // 요청 바디 유효성 검증
            Authentication authentication) { // 인증 객체
        // 서비스 호출 및 댓글 생성
        commentService.createComment(commentRequest, getUserId(authentication));
        return ResponseEntity.status(201).body("Create Comment Successfully."); // 201 Created 응답
    }

    /**
     * 댓글 수정 API
     *
     * @param commentRequest 수정 요청 DTO
     * @param authentication 인증 객체
     * @return 수정 성공 메시지
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @Valid @RequestBody CommentChangeRequest commentRequest, // 요청 바디 유효성 검증
            Authentication authentication) { // 인증 객체
        // 서비스 호출 및 댓글 수정
        commentService.updateComment(commentRequest, getUserId(authentication));
        return ResponseEntity.ok("Update Comment Successfully."); // 200 OK 응답
    }

    /**
     * 댓글 삭제 API
     *
     * @param commentId      댓글 ID (PathVariable)
     * @param authentication 인증 객체
     * @return 삭제 성공 메시지
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long commentId, // 경로 변수
            Authentication authentication) { // 인증 객체
        // 서비스 호출 및 댓글 삭제
        commentService.deleteComment(commentId, getUserId(authentication));
        return ResponseEntity.ok("Delete Comment Successfully."); // 200 OK 응답
    }
}
