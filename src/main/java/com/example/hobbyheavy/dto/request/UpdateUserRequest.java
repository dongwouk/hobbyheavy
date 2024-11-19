package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @NotNull(message = "이름을 입력해주세요.")
    @Size(min = 3, max = 50, message = "이름은 최소 3자리 이상이어야 합니다.")
    private String username;

    private Set<Long> hobbyIds; // 여러 취미 ID

}
