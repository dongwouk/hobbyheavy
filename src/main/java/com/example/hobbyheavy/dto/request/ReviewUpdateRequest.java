package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class ReviewUpdateRequest {

    @NotBlank(message = "작성된 내용이 없습니다.")
    private String content;
    @Min(value = 1, message = "별점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5 이하여야 합니다.")
    private int rating;
}
