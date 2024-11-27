package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class ReviewUpdateRequest {

    @NotBlank(message = "작성된 내용이 없습니다.")
    private String content;
    @Size(min = 1, max = 5, message = "별점은 1 ~ 5 사이의 정수입니다.")
    private Integer rating;
}
