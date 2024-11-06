package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class MeetupSchedule extends Base {

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
    private LocalDateTime activateTime;

    // 일정 상태 (제안, 취소, 확정)
    @Column(name = "status", nullable = true)
    private String status;

    // 일정 참가자
    @Column(name = "participant", nullable = true)
    private String participant;

    // 일정 익명 투표 수
    @Column(name = "votes", nullable = true)
    private Integer votes;

    // 모임 상세 주소
    @Column(name = "location", nullable = true)
    private String location;

}

