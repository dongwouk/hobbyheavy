package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleCancelRequest {
    @NotBlank(message = "취소 이유는 필수 항목입니다.")
    private String reason; // 취소 사유
}
