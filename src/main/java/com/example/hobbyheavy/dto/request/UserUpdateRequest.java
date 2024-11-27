package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {

    @NotNull(message = "이름을 입력해주세요.")
    @Size(min = 3, max = 50, message = "이름은 최소 3자리 이상이어야 합니다.")
    private String username;

    @NotNull(message = "성별을 입력해주세요.")
    private Boolean gender; // 성별

    @NotNull(message = "나이를 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    @Max(value = 120, message = "나이는 120 이하이어야 합니다.")
    private Integer age; // 나이

    private Boolean alarm;

    private Set<Long> hobbyIds; // 여러 취미 ID

}
