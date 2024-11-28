package com.example.hobbyheavy.type;

import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ParticipantStatus {

    WAITING("대기"),
    APPROVED("승인"),
    CANCELED("취소"),
    WITHDRAWN("탈퇴");

    private String description;

    ParticipantStatus(String description) {
        this.description = description;
    }

    @JsonCreator
    public static ParticipantStatus fromString(String inputValue) {
        return Stream.of(ParticipantStatus.values())
                .filter(status -> status.name().equalsIgnoreCase(inputValue))
                .findFirst()
                .orElseThrow(() -> new CustomException(ExceptionCode.PARTICIPANT_STATUS_NOT_FOUND));
    }

    public static ParticipantStatus fromEnum(ParticipantStatus inputValue) {
        return Stream.of(ParticipantStatus.values())
                .filter(status -> status.equals(inputValue))
                .findFirst()
                .orElse(null);
    }
}
