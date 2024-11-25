package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateRequest {

    @NotBlank(message = "기존 비밀번호를 입력해주세요.")
    private String oldPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, max = 100, message = "비밀번호는 최소 8자 이상, 최대 100자 이하이어야 합니다.")
    private String newPassword;

}