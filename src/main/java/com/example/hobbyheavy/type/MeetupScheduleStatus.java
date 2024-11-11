package com.example.hobbyheavy.type;

import lombok.Getter;

@Getter
public enum MeetupScheduleStatus {
    PROPOSED("제안"),
    CONFIRMED("확정"),
    CANCELLED("취소");

    private final String scheduleStatus;

    MeetupScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
