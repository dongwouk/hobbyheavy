package com.example.hobbyheavy.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParticipantApprovedResponse {

    private String userId;
    private String status;
    private String meetupRole;

    private boolean hasVoted;

//    public boolean hasVoted() {
//        return hasVoted;
//    }


}
