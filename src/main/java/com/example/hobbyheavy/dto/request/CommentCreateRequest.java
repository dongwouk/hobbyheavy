package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CommentCreateRequest {

    @NotNull(message = "모임 아이디는 필수입니다.")
    private Long meetupId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    @Size(max = 100, message = "댓글 내용은 100자까지 가능합니다.")
    private String content;
}