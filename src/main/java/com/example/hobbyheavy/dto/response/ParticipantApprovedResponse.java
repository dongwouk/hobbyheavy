package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.type.ParticipantStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantApprovedResponse {

    private String userId;
    private ParticipantStatus status;
    private String meetupRole;

    private boolean hasVoted;

//    public boolean hasVoted() {
//        return hasVoted;
//    }


}
