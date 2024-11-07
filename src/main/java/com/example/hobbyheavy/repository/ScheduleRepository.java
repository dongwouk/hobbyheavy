package com.example.hobbyheavy.repository;


import com.example.hobbyheavy.entity.MeetupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<MeetupSchedule, Long> {
    List<MeetupSchedule> findByMeetupMeetupId(Long meetupId);
    List<MeetupSchedule> findByStatus(String status);
    List<MeetupSchedule> findByParticipant(String participant);
}
