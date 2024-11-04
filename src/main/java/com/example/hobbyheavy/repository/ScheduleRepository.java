package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 특정 모임의 모든 일정 조회
    List<Schedule> findByMeetupId(Long meetupId);
}