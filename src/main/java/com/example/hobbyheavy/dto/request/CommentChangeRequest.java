package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentChangeRequest {

    @NotNull(message = "모임 아이디는 필수입니다.")
    private Long meetupId;

    @NotNull(message = "댓글 아이디는 필수입니다.")
    private Long commentId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;
}
