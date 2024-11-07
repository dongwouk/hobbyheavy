package com.example.hobbyheavy.dto.request;

import com.example.hobbyheavy.entity.Meetup;
import com.example.hobbyheavy.entity.MeetupSchedule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleRequest {
    private Long meetupId;
    private LocalDateTime proposalDate;
    private LocalDateTime activateTime;
    private String status;
    private String participant;
    private Integer votes;
    private String location;

    public static MeetupSchedule toEntity(ScheduleRequest dto) {
        return MeetupSchedule.builder()
                .meetup(Meetup.builder().meetupId(dto.getMeetupId()).build())
                .proposalDate(dto.getProposalDate())
                .activateTime(dto.getActivateTime())
                .status(dto.getStatus())
                .participant(dto.getParticipant())
                .votes(dto.getVotes())
                .location(dto.getLocation())
                .build();
    }


}
