package com.example.hobbyheavy.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class MeetupUpdateRequest {

    @Size(min = 1, max = 50, message = "모임 이름은 최소 1자부터 최대 50자까지 가능합니다.")
    private String meetupName;

    @NotBlank(message = "모임 설명은 필수입니다.")
    private String description;

    @NotBlank(message = "모임 위치는 필수입니다.")
    private String location;

    @NotBlank(message = "모임 반복 규칙은 필수입니다.")
    private String recurrenceRule;

    @Min(value = 1, message = "모임 참여자는 최소 1명 이상이어야 합니다.")
    @Max(value = 100, message = "모임 참여자는 최대 100명 이하입니다.")
    private Integer maxParticipants;

}
