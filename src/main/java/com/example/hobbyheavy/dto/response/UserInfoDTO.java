package com.example.hobbyheavy.dto.response;

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
public class UserInfoDTO {

    private Long id;
    private String userId;
    private String username;
    private String email;
    private LocalDateTime createdAt;

    public UserInfoDTO toUserInfoDTO(User user) {
        return UserInfoDTO.builder()
                .id(user.getId())
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedDate())
                .build();
    }

}
