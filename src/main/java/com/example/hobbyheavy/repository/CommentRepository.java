package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Comment;
import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByMeetup_MeetupId(Long meetupId);
}
