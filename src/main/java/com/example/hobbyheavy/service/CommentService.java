package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.CommentCreateRequest;
import com.example.hobbyheavy.entity.Comment;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.CommentRepository;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;

    public List<Comment> meetupComments (Long meetupId) {
        return commentRepository.findAllByMeetup_MeetupId(meetupId);
    }

    /** 댓글 생성 **/
    public void createComment (CommentCreateRequest request, String userId) {
        User user = userRepository.findByUserId(userId);
        Meetup meetup = meetupRepository.findFirstByMeetupId(request.getMeetupId())
                .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
        commentRepository.save(Comment.builder()
                .user(user).meetup(meetup).content(request.getContent()).build());
    }
}
