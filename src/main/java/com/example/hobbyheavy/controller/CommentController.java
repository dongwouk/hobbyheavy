package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.CommentCreateRequest;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    private String getUserId(Authentication authentication) {
        if(authentication == null || !authentication.isAuthenticated()) {
            throw new CustomException(ExceptionCode.UNAUTHORIZED_USER);
        }
        return authentication.getName();
    }

    @PostMapping
    public ResponseEntity<String> createComment
            (@Valid @RequestBody CommentCreateRequest commentCreateRequest,
             Authentication authentication){
        commentService.createComment(commentCreateRequest, getUserId(authentication));
        return ResponseEntity.ok("Create Comment Successfully.");
    }
}