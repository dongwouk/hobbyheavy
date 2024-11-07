package com.example.hobbyheavy.type;

import lombok.Getter;

@Getter
public enum ParticipantRole {

    MEMBER("일반"),
    HOST("모임장"),
    SUB_HOST("부모임장");

    private String role;

    ParticipantRole (String role) {
        this.role = role;
    }
}
