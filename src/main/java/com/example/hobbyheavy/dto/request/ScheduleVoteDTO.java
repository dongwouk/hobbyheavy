package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleVoteDTO {

    private Long scheduleId;
    private UserEntity user;
}
