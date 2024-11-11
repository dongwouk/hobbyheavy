package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Boolean existsByMeetupName(String name);

    Optional<Meetup> findFirstByMeetupId(Long id);

    @Query("SELECT m.hostUser.userId FROM Meetup m WHERE m.meetupId = :meetupId")
    String findHostNameByMeetupId(@Param("meetupId") Long meetupId);
}
