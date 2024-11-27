package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.entity.MeetupSchedule;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleResponse {
    private Long scheduleId;
    private Long meetupId;
    private LocalDateTime proposalDate;
    private String activateTime;
    private String status;
    private int voteCount; // 투표한 사용자 수
    private String location;
    private LocalDateTime votingDeadline;

    public static ScheduleResponse fromEntity(MeetupSchedule entity) {
        if (entity == null) {
            return null; // entity가 null인 경우 null 반환
        }
        return ScheduleResponse.builder()
                .scheduleId(entity.getScheduleId())
                .meetupId(entity.getMeetup().getMeetupId())
                .proposalDate(entity.getProposalDate())
                .activateTime(entity.getActivateTime())
                .status(entity.getScheduleStatus().name())
                .voteCount(entity.getVotes().size()) // Set<String> votes의 크기를 이용해 투표자 수를 반환
                .location(entity.getLocation())
                .votingDeadline(entity.getVotingDeadline())
                .build();
    }
}
