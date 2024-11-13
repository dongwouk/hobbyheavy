package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMeetup_MeetupId(Long meetupId);

    Optional<Participant> findByMeetup_MeetupIdAndUser_UserId(Long meetupId, String userId);

    List<Participant> findAllByUser_Id(Long userId);

}
