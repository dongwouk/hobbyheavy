package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.ReviewCreateRequest;
import com.example.hobbyheavy.dto.request.ReviewUpdateRequest;
import com.example.hobbyheavy.dto.response.ReviewResponse;
import com.example.hobbyheavy.entity.Participant;
import com.example.hobbyheavy.entity.Review;
import com.example.hobbyheavy.entity.Schedule;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.ParticipantRepository;
import com.example.hobbyheavy.repository.ReviewRepository;
import com.example.hobbyheavy.repository.ScheduleRepository;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.ParticipantStatus;
import com.example.hobbyheavy.type.ScheduleStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final ParticipantRepository participantRepository;

    public Page<ReviewResponse> getReviews(Long scheduleId, String userId, int page, int size) {
        isMeetupMember(scheduleId, userId);
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findAllBySchedule_ScheduleIdOrderByCreatedDateDesc(
                scheduleId, pageable);
        return reviews.map(ReviewResponse::new);
    }

    public ReviewResponse getReviewById(Long reviewId, String userId) {
        Review review = getReview(reviewId);
        isMeetupMember(review.getSchedule().getScheduleId(), userId);
        return new ReviewResponse(review);
    }

    public void createReview(ReviewCreateRequest request, String userId) {
        isMeetupMember(request.getMeetupId(), userId);
        isJoinSchedule(request.getScheduleId(), userId);
        Review review = Review.builder()
                .schedule(scheduleRepository.findById(request.getScheduleId())
                        .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND)))
                .user(userRepository.findByUserId(userId)
                        .orElseThrow(() -> new CustomException(ExceptionCode.USER_NOT_FOUND)))
                .content(request.getContent())
                .rating(request.getRating())
                .build();
        reviewRepository.save(review);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateRequest request, String userId) {
        Review review = getReview(reviewId);
        isReviewer(review, userId);
        try {
            review.updateContent(request.getContent());
            review.updateRating(request.getRating());
        } catch (Exception e) {
            log.error("후기 수정 실패 : {}, 상세 에러 메세지 : {}", reviewId, e.getMessage());
            throw new CustomException(ExceptionCode.REVIEW_UPDATE_FAILED);
        }
    }

    @Transactional
    public void deleteReviewById(Long reviewId, String userId) {
        Review review = getReview(reviewId);
        isReviewer(review, userId);
        try {
            review.markAsDeleted();
        } catch (Exception e) {
            log.error("후기 삭제 실패 : {}, 상세 에러 메세지 : {}", reviewId, e.getMessage());
            throw new CustomException(ExceptionCode.REVIEW_DELETE_FAILED);
        }
    }

    private void isReviewer(Review review, String userId) {
        if (!Objects.equals(review.getUser().getUserId(), userId)) {
            throw new CustomException(ExceptionCode.INVALID_REVIEWER);
        }
    }

    private void isMeetupMember(Long meetupId, String userId) {
        Participant participant = participantRepository.findByMeetup_MeetupIdAndUser_UserId(meetupId, userId)
                .orElseThrow(() -> new CustomException(ExceptionCode.PARTICIPANT_NOT_FOUND));
        if(participant.getStatus() == ParticipantStatus.CANCELED ||
                participant.getStatus() == ParticipantStatus.WITHDRAWN) {
            throw new CustomException(ExceptionCode.PARTICIPANT_NOT_FOUND);
        }
    }

    private void isJoinSchedule(Long scheduleId, String userId) {
        Schedule schedule = scheduleRepository.findByScheduleIdAndDeletedFalse(scheduleId)
                .orElseThrow(() -> new CustomException(ExceptionCode.SCHEDULE_NOT_FOUND));
        if (!schedule.getVotes().contains(userId)) {
            throw new CustomException(ExceptionCode.SCHEDULE_NOT_JOIN);
        }
    }

    /**
     * 스케쥴 검증
     * @param reviewId
     * @return review
     */
    private Review getReview (Long reviewId) {
        Review review = reviewRepository.findFirstByReviewId(reviewId)
                .orElseThrow(() -> new CustomException(ExceptionCode.REVIEW_NOT_FOUND));

        if (review.getSchedule().getScheduleStatus() != ScheduleStatus.CONFIRMED) {
            throw new CustomException(ExceptionCode.SCHEDULE_NOT_CONFIRMED);
        }

        if (review.getSchedule().getProposalDate().toLocalDate().isBefore(LocalDate.now())) {
            throw new CustomException(ExceptionCode.SCHEDULE_IS_BEFORE);
        }
        return review;
    }

}
