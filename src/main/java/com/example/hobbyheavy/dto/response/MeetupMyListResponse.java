package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Hobby;
import com.example.hobbyheavy.entity.Meetup;
import lombok.Getter;

@Getter
public class MeetupMyListResponse {

    private final Long meetupId;
    private final String meetupName;
    private final String location;
    private final String hobby;

    public MeetupMyListResponse(Meetup meetup, Hobby hobby) {
        this.meetupId = meetup.getMeetupId();
        this.meetupName = meetup.getMeetupName();
        this.location = meetup.getLocation();
        this.hobby = hobby.getHobbyName();
    }
}
