package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Meetup;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class MeetupInfoResponse {

    private Long meetupId;
    private String meetupName;
    private String description;
    private String hostName;
    private String location;
    private String recurrenceRule;
    private LocalDate nextOccurrence;
    private LocalTime startTime;
    private LocalTime endTime;
    private int maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MeetupInfoResponse (Meetup meetup) {
        this.meetupId = meetup.getMeetupId();
        this.meetupName = meetup.getMeetupName();
        this.description = meetup.getDescription();
        this.hostName = meetup.getUserId().getUserId();
        this.location = meetup.getLocation();
        this.recurrenceRule = meetup.getRecurrenceRule();
        this.nextOccurrence = meetup.getNextOccurrence();
        this.startTime = meetup.getStartTime();
        this.endTime = meetup.getEndTime();
        this.maxParticipants = meetup.getMaxParticipants();
        this.createdAt = meetup.getCreatedDate();
        this.updatedAt = meetup.getModifiedDate();
    }
}
