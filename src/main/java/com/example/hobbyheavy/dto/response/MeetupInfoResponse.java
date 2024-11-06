package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Meetup;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
public class MeetupInfoResponse {

    private final Long meetupId;
    private final String meetupName;
    private final String description;
    private final String hostName;
    private final String location;
    private final String recurrenceRule;
    private final int maxParticipants;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public MeetupInfoResponse (Meetup meetup) {
        this.meetupId = meetup.getMeetupId();
        this.meetupName = meetup.getMeetupName();
        this.description = meetup.getDescription();
        this.hostName = meetup.getHostUser().getUserId();
        this.location = meetup.getLocation();
        this.recurrenceRule = meetup.getRecurrenceRule();
        this.maxParticipants = meetup.getMaxParticipants();
        this.createdAt = meetup.getCreatedDate();
        this.updatedAt = meetup.getUpdatedDate();
    }
}
