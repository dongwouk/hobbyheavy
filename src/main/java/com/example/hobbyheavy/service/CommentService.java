package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.Comment;
import com.example.hobbyheavy.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> meetupComments (Long meetupId) {
        return commentRepository.findAllByMeetup_MeetupId(meetupId);
    }
}
