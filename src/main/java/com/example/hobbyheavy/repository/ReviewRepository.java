package com.example.hobbyheavy.repository;
;
import com.example.hobbyheavy.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findAllBySchedule_ScheduleIdOrderByCreatedDateDesc(Long scheduleId, Pageable pageable);

    Optional<Review> findFirstByReviewId(Long id);


}
