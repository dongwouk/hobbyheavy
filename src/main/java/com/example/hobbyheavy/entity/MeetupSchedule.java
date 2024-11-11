package com.example.hobbyheavy.entity;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.type.MeetupScheduleStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeetupSchedule extends Base{

    // 일정 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    // 모임 고유 ID
    @ManyToOne
    @JoinColumn(name = "meetup_id", nullable = false)
    private Meetup meetup;

    // 제안된 날짜 및 시간
    @Column(name = "proposal_date", nullable = true)
    private LocalDateTime proposalDate;

    // 모임 활동 시간
    @Column(name = "activate_time", nullable = true)
    private Duration activateTime;

    // 일정 상태 (제안, 취소, 확정)
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = true)
    private MeetupScheduleStatus scheduleStatus = MeetupScheduleStatus.PROPOSED;

    // 일정 참가자
    @Column(name = "participant", nullable = true)
    private String participant;

    // 일정 익명 투표 수
    @Column(name = "votes", nullable = true)
    private Integer votes;

    // 모임 상세 주소
    @Column(name = "location", nullable = true)
    private String location;

    // 투표 마감일
    // 투표 마감일
    @Column(name = "voting_deadline", nullable = true)
    private LocalDateTime votingDeadline;

    // 취소 이유 (optional)
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    public void updateFromDTO(ScheduleRequest request) {
        this.proposalDate = request.getProposalDate();
        this.activateTime = null; // activateDuration은 다른 곳에서 계산되어 설정됨
        this.scheduleStatus = MeetupScheduleStatus.valueOf(request.getStatus().toUpperCase());
        this.participant = request.getParticipant();
        this.votes = request.getVotes();
        this.location = request.getLocation();
        this.votingDeadline = null; // votingDeadline은 다른 곳에서 계산되어 설정됨
    }

    public void setStatus(MeetupScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public void setCancellationReason(String reason) {
        this.cancellationReason = reason;
    }
}

