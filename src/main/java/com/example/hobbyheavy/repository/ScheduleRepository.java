package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.MeetupSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleRepository extends JpaRepository<MeetupSchedule, Long> {

}
