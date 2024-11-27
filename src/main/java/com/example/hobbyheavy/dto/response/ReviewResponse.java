package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Review;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ReviewResponse {

    private final Long reviewId;
    private final Long reviewerId;
    private final Long scheduleId;
    private final String content;
    private final Integer rating;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public ReviewResponse(Review review) {
        this.reviewId = review.getReviewId();
        this.reviewerId = review.getUser().getId();
        this.scheduleId = review.getSchedule().getScheduleId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.createdAt = review.getCreatedDate();
        this.updatedAt = review.getUpdatedDate();
    }
}
