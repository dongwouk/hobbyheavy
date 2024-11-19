package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Hobby;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HobbyResponse {

    private Long hobbyId;
    private String hobbyName;

    public static HobbyResponse fromEntity (Hobby hobby) {
        return HobbyResponse.builder()
                .hobbyId(hobby.getHobbyId())
                .hobbyName(hobby.getHobbyName())
                .build();
    }
}
