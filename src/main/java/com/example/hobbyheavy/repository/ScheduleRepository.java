package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.MeetupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScheduleRepository extends JpaRepository<MeetupSchedule, Long> {
    List<MeetupSchedule> findAllByMeetup_MeetupIdIn(List<Long> meetupIds);
    Optional<MeetupSchedule> findByScheduleIdAndDeletedFalse(Long scheduleId);
    List<MeetupSchedule> findAllByMeetup_MeetupIdInAndDeletedFalse(List<Long> meetupIds);
}
