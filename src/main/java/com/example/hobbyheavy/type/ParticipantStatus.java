package com.example.hobbyheavy.type;

import lombok.Getter;

@Getter
public enum ParticipantStatus {

    WAITING("대기"),
    APPROVED("승인"),
    CANCELED("취소"),
    WITHDRAWN("탈퇴")
    ;

    private final String status;

    ParticipantStatus(String status) {
        this.status = status;
    }
}
