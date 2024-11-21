package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Meetup;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class MeetupInfoResponse {

    private final Long meetupId;
    private final String meetupName;
    private final String description;
    private final String thumbnail;
    private final String hostName;
    private final String location;
    private final String recurrenceRule;
    private final int maxParticipants;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    private final List<CommentResponse> comments;
    private final List<ParticipantApprovedResponse> participants;

    public MeetupInfoResponse (Meetup meetup, List<CommentResponse> comments, List<ParticipantApprovedResponse> participants) {
        this.meetupId = meetup.getMeetupId();
        this.meetupName = meetup.getMeetupName();
        this.description = meetup.getDescription();
        this.thumbnail = meetup.getThumbnail();
        this.hostName = meetup.getHostUser().getUserId();
        this.location = meetup.getLocation();
        this.recurrenceRule = meetup.getRecurrenceRule();
        this.maxParticipants = meetup.getMaxParticipants();
        this.createdAt = meetup.getCreatedDate();
        this.updatedAt = meetup.getUpdatedDate();
        this.comments = comments;
        this.participants = participants;
    }
}
