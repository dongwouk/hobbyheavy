package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequest {
    private Long meetupId;
    private LocalDateTime proposalDate;
    private String activateTime;
    private String votingDeadline; // 투표 마감 시간 (예: "3시간")
    private String status;
    private String participant;
    private Integer votes;
    private String location;

    public static MeetupSchedule toEntity(ScheduleRequest dto) {
        return MeetupSchedule.builder()
                .meetup(Meetup.builder().meetupId(dto.getMeetupId()).build())
                .proposalDate(dto.getProposalDate())
                .activateTime(null) // activateTime은 다른 곳에서 계산되어 설정됨
                .scheduleStatus(dto.getStatus() != null ? MeetupScheduleStatus.valueOf(dto.getStatus().toUpperCase()) : MeetupScheduleStatus.PROPOSED)
                .participant(dto.getParticipant())
                .votes(dto.getVotes())
                .location(dto.getLocation())
                .votingDeadline(null) // votingDeadline은 다른 곳에서 계산되어 설정됨
                .build();
    }


}
