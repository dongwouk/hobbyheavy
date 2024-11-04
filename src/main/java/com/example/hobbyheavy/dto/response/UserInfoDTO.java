package com.example.hobbyheavy.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserInfoDTO {

    private Long userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;

}
