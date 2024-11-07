package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    List<Participant> findAllByMeetup_MeetupId(Long meetupId);

    Participant findByMeetup_MeetupIdAndUser_UserId(Long meetupId, String userId);
}
