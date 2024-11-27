package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDeleteRequest {

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;

}
