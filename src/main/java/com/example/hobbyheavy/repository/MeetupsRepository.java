package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Meetups;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface MeetupsRepository extends JpaRepository<Meetups, Long> {

    Boolean existsByMeetupName(String name);

    Optional<Meetups> findFirstByMeetupId(Long id);

    @Transactional
    void deleteMeetupsByMeetupId(Long id);
}
