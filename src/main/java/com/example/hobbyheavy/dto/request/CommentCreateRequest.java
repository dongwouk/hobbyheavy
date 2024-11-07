package com.example.hobbyheavy.dto.request;

import lombok.Getter;

@Getter
public class CommentCreateRequest {

    private Long meetupId;
    private String content;
}