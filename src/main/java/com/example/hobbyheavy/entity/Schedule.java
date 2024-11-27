package com.example.hobbyheavy.entity;

import com.example.hobbyheavy.dto.request.ScheduleRequest;
import com.example.hobbyheavy.type.ScheduleStatus;
import com.example.hobbyheavy.util.ScheduleUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Schedule extends Base {

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
    private String activateTime;

    // 일정 상태 (제안, 취소, 확정)
    @Enumerated(EnumType.STRING)
    @Column(name = "schedule_status", nullable = true)
    private ScheduleStatus scheduleStatus = ScheduleStatus.PROPOSED;

    // 일정 투표한 사용자 ID 목록
    @ElementCollection
    @CollectionTable(name = "schedule_votes", joinColumns = @JoinColumn(name = "schedule_id"))
    @Column(name = "user_id")
    @Builder.Default
    private Set<String> votes = new HashSet<>();

    // 모임 상세 주소
    @Column(name = "location", nullable = true)
    private String location;

    // 투표 마감일
    @Column(name = "voting_deadline", nullable = true)
    private LocalDateTime votingDeadline;

    // 취소 이유 (optional)
    @Column(name = "cancellation_reason")
    private String cancellationReason;

    public void updateFromDTO(ScheduleRequest request) {
        if (request.getProposalDate() != null) {
            this.proposalDate = request.getProposalDate();
        }
        if (request.getActivateTime() != null) {
            this.activateTime = request.getActivateTime();
        }
        if (request.getLocation() != null) {
            this.location = request.getLocation();
        }
        if (request.getVotingDeadline() != null) {
            // DurationParser를 사용해 요청된 문자열 형식을 LocalDateTime으로 변환 후 votingDeadline에 설정
            this.votingDeadline = ScheduleUtil.calculateVotingDeadline(request);
        }
    }

    public void setStatus(ScheduleStatus scheduleStatus) {
        this.scheduleStatus = scheduleStatus;
    }

    public void setCancellationReason(String reason) {
        this.cancellationReason = reason;
    }

    /**
     * 사용자 투표 추가 메서드.
     * @param userId 투표한 사용자 ID
     */
    public void addVote(String userId) {
        votes.add(userId);
    }

    /**
     * 사용자 투표 제거 메서드.
     * @param userId 투표 취소할 사용자 ID
     */
    public void removeVote(String userId) {
        votes.remove(userId);
    }

}
