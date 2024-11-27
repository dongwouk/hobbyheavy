package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequest {
    private Long meetupId;
    @FutureOrPresent(message = "제안 날짜는 미래 또는 현재여야 합니다")
    private LocalDateTime proposalDate;
    private String activateTime;
    private String votingDeadline; // 투표 마감 시간 (예: "3시간")
    private String status;
    private String location;

    public static MeetupSchedule toEntity(ScheduleRequest dto) {
        return MeetupSchedule.builder()
                .meetup(Meetup.builder().meetupId(dto.getMeetupId()).build())
                .proposalDate(dto.getProposalDate())
                .activateTime(dto.getActivateTime())
                .scheduleStatus(dto.getStatus() != null ? MeetupScheduleStatus.valueOf(dto.getStatus().toUpperCase()) : MeetupScheduleStatus.PROPOSED)
                .location(dto.getLocation())
                .votingDeadline(null) // votingDeadline은 다른 곳에서 계산되어 설정됨
                .build();
    }


}
