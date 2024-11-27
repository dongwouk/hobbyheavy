package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserJoinRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 3, max = 50, message = "아이디는 최소 3자 이상, 최대 50자 이하이어야 합니다.")
    private String userId; // 로그인용 ID

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 3, max = 50, message = "이름은 최소 3자리 이상이어야 합니다.")
    private String username; // 유저의 이름

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상, 최대 100자 이하이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Size(max = 50, message = "이메일은 최대 50자 이하이어야 합니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotNull(message = "성별을 입력해주세요.")
    private Boolean gender;

    @NotNull(message = "나이를 입력해주세요.")
    @Min(value = 0, message = "나이는 0 이상이어야 합니다.")
    @Max(value = 120, message = "나이는 120 이하이어야 합니다.")
    private Integer age;

    private Set<Long> hobbyIds; // 여러 취미 ID를 받을 수 있도록 변경

    @Builder.Default
    private Boolean alarm = true;

}
