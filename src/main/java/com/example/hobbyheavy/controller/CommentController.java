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
            (@Valid @RequestBody CommentCreateRequest commentRequest,
             Authentication authentication){
        commentService.createComment(commentRequest, getUserId(authentication));
        return ResponseEntity.ok("Create Comment Successfully.");
    }

    @PutMapping
    public ResponseEntity<String> updateComment
            (@Valid @RequestBody CommentChangeRequest commentRequest, Authentication authentication) {
        commentService.updateComment(commentRequest, getUserId(authentication));
        return ResponseEntity.ok("Create Comment Successfully.");
    }

    @DeleteMapping
    public ResponseEntity<String> deleteComment (
            @Valid @RequestBody CommentChangeRequest commentRequest, Authentication authentication) {
        commentService.deleteComment(commentRequest, getUserId(authentication));
        return  ResponseEntity.ok("Delete Comment Successfully.");
    }
}