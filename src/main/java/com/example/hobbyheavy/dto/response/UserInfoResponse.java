package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Hobby;
import com.example.hobbyheavy.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserInfoResponse {

    private Long id;
    private String userId;
    private String username;
    private String email;
    private Boolean gender;
    private Integer age;
    private Hobby hobby; // 취미 ID
    private Boolean alarm; // 알림구독 여부
    private LocalDateTime createdAt;

    public UserInfoResponse toUserInfoDTO(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .gender(user.getGender())
                .age(user.getAge())
                .hobby(user.getHobby())
                .alarm(user.getAlarm())
                .createdAt(user.getCreatedDate())
                .build();
    }

}
