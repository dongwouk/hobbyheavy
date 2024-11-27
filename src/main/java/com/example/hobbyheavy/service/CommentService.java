package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.CommentChangeRequest;
import com.example.hobbyheavy.dto.request.CommentCreateRequest;
import com.example.hobbyheavy.dto.response.CommentResponse;
import com.example.hobbyheavy.entity.Comment;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.CommentRepository;
import com.example.hobbyheavy.repository.MeetupRepository;
import com.example.hobbyheavy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final MeetupRepository meetupRepository;

    public List<CommentResponse> meetupComments(Long meetupId) {
        return commentRepository.findAllByMeetup_MeetupId(meetupId)
                .stream().map(CommentResponse::new).toList();
    }

    /**
     * 댓글 생성
     **/
    public void createComment(CommentCreateRequest request, String userId) {
        User user = getUser(userId);
        Meetup meetup = meetupRepository.findFirstByMeetupId(request.getMeetupId())
                .orElseThrow(() -> new CustomException(ExceptionCode.MEETUP_NOT_FOUND));
        commentRepository.save(Comment.builder()
                .user(user).meetup(meetup).content(request.getContent()).build());
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public void updateComment(CommentChangeRequest request, String userId) {
        Comment comment = getComment(request, getUser(userId));
        comment.updateComment(request.getContent());
    }

    /**
     * 댓글 삭제
     */
    public void deleteComment(CommentChangeRequest request, String userId) {
        try {
            Comment comment = getComment(request, getUser(userId));
            comment.markAsDeleted();
            commentRepository.save(comment);
        } catch (Exception e) {
            log.error("commentId : {}, 댓글 삭제 중 에러 발생 : {}", request.getCommentId(), e.getMessage());
            throw new CustomException(ExceptionCode.COMMENT_DELETE_FAILED);
        }
    }

    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND));
    }


    private Comment getComment(CommentChangeRequest request, User user) {
        Comment comment = commentRepository.findById(request.getCommentId())
                .orElseThrow(() -> new CustomException(ExceptionCode.COMMENT_NOT_FOUND));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new CustomException(ExceptionCode.COMMENT_USER_MISMATCH);
        }
        return comment;
    }
}
