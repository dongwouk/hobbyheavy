package com.example.hobbyheavy.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meetups extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetup_id")
    private Long meetupId;

    @Column(name = "meetup_name", unique = true, length = 50, nullable = false)
    private String meetupName;

    @Column(length = 200, nullable = false)
    private String description;

    @Column(length = 100, nullable = false)
    private String location;

    @Column(name = "recurrence_rule", length = 10, nullable = false)
    private String recurrenceRule;

    @Column(name = "next_occurrence")
    private LocalDate nextOccurrence;

    @Column(name = "start_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime startTime;

    @Column(name = "end_time")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private LocalTime endTime;

    @Column(name = "max_participants", nullable = false)
    private int maxParticipants;

    @ManyToOne
    @JoinColumn(name = "hobby_id")
    private Hobby hobbyId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    public void updateMeetupName(String newMeetupName) {
        this.meetupName = newMeetupName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateRecurrenceRule(String newRecurrenceRule) {
        this.recurrenceRule = newRecurrenceRule;
    }

    public void updateNextOccurrence(LocalDate newNextOccurrence) {
        this.nextOccurrence = newNextOccurrence;
    }

    public void updateStartTime(LocalTime newStartTime) {
        this.startTime = newStartTime;
    }

    public void updateEndTime(LocalTime newEndTime) {
        this.endTime = newEndTime;
    }

    public void updateMaxParticipants(int newMaxParticipants) {
        this.maxParticipants = newMaxParticipants;
    }

    public void updateHobby(Hobby newHobby) {
        this.hobbyId = newHobby;
    }
}
