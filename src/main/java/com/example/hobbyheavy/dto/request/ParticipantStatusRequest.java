package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.type.ParticipantStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParticipantStatusRequest {

    @NotNull(message = "모임 아이디는 필수입니다.")
    private Long meetupId;
    @NotBlank(message = "변경할 아이디는 필수입니다.")
    private String userId;
    @NotNull(message = "모임 권한은 필수입니다.")
    private ParticipantStatus status;
}
