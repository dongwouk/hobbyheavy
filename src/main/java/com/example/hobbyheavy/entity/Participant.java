package com.example.hobbyheavy.entity;

import com.example.hobbyheavy.type.ParticipantRole;
import com.example.hobbyheavy.type.ParticipantStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    @Enumerated(EnumType.STRING)
    private ParticipantStatus status;

    @Column(name = "meetup_role", nullable = false)
    private String meetupRole;

    @Column(name = "meetup_alram", nullable = false)
    private Boolean meetupAlarm = true;

    public void updateStatus(ParticipantStatus status) {
        this.status = status;
    }

    public void updateMeetupRole(ParticipantRole role) { this.meetupRole = role.getRole(); }

    public Boolean updateMeetupAlarm() {
        this.meetupAlarm = !this.meetupAlarm;
        return this.meetupAlarm;
    }
}
