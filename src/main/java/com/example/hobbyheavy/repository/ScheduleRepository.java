package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findAllByMeetup_MeetupIdIn(List<Long> meetupIds);
    Optional<Schedule> findByScheduleIdAndDeletedFalse(Long scheduleId);
    List<Schedule> findAllByMeetup_MeetupIdInAndDeletedFalse(List<Long> meetupIds);
}
