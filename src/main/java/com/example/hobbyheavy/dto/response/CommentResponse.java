package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Comment;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResponse {

    private final Long commentId;
    private final Long userId;
    private final String userName;
    private final String comment;
    private final LocalDateTime createTime;
    private final LocalDateTime updateTime;

    public CommentResponse(Comment comment) {
        this.commentId = comment.getCommentId();
        this.userId = comment.getUser().getId();
        this.userName = comment.getUser().getUsername();
        this.comment = comment.getContent();
        this.createTime = comment.getCreatedDate();
        this.updateTime = comment.getUpdatedDate();
    }
}
