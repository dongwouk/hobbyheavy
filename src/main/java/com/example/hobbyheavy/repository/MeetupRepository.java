package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeetupRepository extends JpaRepository<Meetup, Long> {

    Optional<Meetup> findFirstByMeetupId(Long id);

    List<Meetup> findAllByHostUser(User user);

    @Query("SELECT m.hostUser.userId FROM Meetup m WHERE m.meetupId = :meetupId")
    String findHostNameByMeetupId(@Param("meetupId") Long meetupId);

    Page<Meetup> findAllByOrderByCreatedDateDesc(Pageable pageable);

    Page<Meetup> findAllByHobby_HobbyNameOrderByCreatedDateDesc(Pageable pageable, String hobbyName);

    Page<Meetup> findAllByMeetupNameContainingOrDescriptionContaining(Pageable pageable, String meetupNameKeyword, String descriptionKeyword);

    Page<Meetup> findAllByLocationContaining(Pageable pageable, String location);
}
