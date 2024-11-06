package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Participant extends Base{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "participant_id")
    private Long participantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetup_id", nullable = false)
    private Meetup meetup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "status", length = 10, nullable = false)
    private String status = "대기중";

    @Column(name = "meetup_role", nullable = false)
    private String meetupRole;

    @Column(name = "meetup_alram", nullable = false)
    private Boolean meetupAlarm = true;

    public void updateStatus(String status) {
        this.status = status;
    }

}
