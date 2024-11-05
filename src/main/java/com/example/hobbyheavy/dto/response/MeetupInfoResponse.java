package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Meetups;
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

    public MeetupInfoResponse (Meetups meetups) {
        this.meetupId = meetups.getMeetupId();
        this.meetupName = meetups.getMeetupName();
        this.description = meetups.getDescription();
        this.hostName = meetups.getUserId().getUserId();
        this.location = meetups.getLocation();
        this.recurrenceRule = meetups.getRecurrenceRule();
        this.nextOccurrence = meetups.getNextOccurrence();
        this.startTime = meetups.getStartTime();
        this.endTime = meetups.getEndTime();
        this.maxParticipants = meetups.getMaxParticipants();
        this.createdAt = meetups.getCreatedDate();
        this.updatedAt = meetups.getModifiedDate();
    }
}
