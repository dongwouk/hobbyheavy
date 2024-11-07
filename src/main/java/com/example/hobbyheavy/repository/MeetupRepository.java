package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Meetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Boolean existsByMeetupName(String name);

    Optional<Meetup> findFirstByMeetupId(Long id);
}
