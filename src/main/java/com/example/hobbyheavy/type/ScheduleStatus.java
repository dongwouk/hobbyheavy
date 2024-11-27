package com.example.hobbyheavy.type;

import lombok.Getter;

@Getter
public enum ScheduleStatus {
    PROPOSED("제안"),
    CONFIRMED("확정"),
    CANCELLED("취소");

    private final String scheduleStatus;

    ScheduleStatus(String scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }
}
