package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.Hobby;
import jakarta.validation.constraints.Email;
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
public class JoinResponse {

    @NotNull
    @Size(min = 3, max = 50, message = "아이디는 최소 3자리 이상이어야 합니다.")
    private String userId; // 로그인용 ID

    @NotNull
    @Size(min = 3, max = 50, message = "이름은 최소 3자리 이상이어야 합니다.")
    private String username; // 유저의 이름

    @NotNull
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자리 이상이어야 합니다.")
    private String password;

    @NotNull
    @Email
    private String email;

    @NotNull
    private Boolean gender;

    @NotNull
    private Integer age;

    private Hobby hobby;

}
