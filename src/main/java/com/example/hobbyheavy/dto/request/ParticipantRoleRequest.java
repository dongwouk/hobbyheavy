package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.type.ParticipantRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ParticipantRoleRequest {

    @NotNull(message = "모임 아이디는 필수입니다.")
    private Long meetupId;
    @NotBlank(message = "변경할 아이디는 필수입니다.")
    private String userId;
    @NotNull(message = "변경할 권한은 필수입니다.")
    private ParticipantRole role;
}
