package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.User;
import lombok.Getter;

@Getter
public class ParticipantWaitResponse {

    private long participantId;
    private String participantName;
    private Boolean gender;
    private Integer age;

    public ParticipantWaitResponse(User user) {
        this.participantId = user.getId();
        this.participantName = user.getUserId();
        this.gender = user.getGender();
        this.age = user.getAge();
    }
}
