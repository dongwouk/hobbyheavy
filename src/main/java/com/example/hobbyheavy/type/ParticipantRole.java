package com.example.hobbyheavy.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.stream.Stream;

@Getter
public enum ParticipantRole {

    MEMBER("일반"),
    HOST("모임장"),
    SUB_HOST("부모임장");

    private String role;

    ParticipantRole (String role) {
        this.role = role;
    }

    @JsonCreator
    public static ParticipantRole parsing(String inputValue) {
        return Stream.of(ParticipantRole.values())
                .filter(role -> role.name().equalsIgnoreCase(inputValue))
                .findFirst()
                .orElse(null);
    }
}
