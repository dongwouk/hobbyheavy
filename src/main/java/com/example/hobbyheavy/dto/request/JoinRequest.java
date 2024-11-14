package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.Hobby;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 3, max = 50, message = "아이디는 최소 3자리 이상이어야 합니다.")
    private String userId; // 로그인용 ID

    @NotBlank(message = "이름을 입력해주세요.")
    @Size(min = 3, max = 50, message = "이름은 최소 3자리 이상이어야 합니다.")
    private String username; // 유저의 이름

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email
    private String email;

    @NotNull(message = "성별을 입력해주세요.")
    private Boolean gender;

    @NotNull(message = "나이를 입력해주세요.")
    private Integer age;

    private Hobby hobby;

}
