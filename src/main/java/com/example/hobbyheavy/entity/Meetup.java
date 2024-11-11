package com.example.hobbyheavy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Meetup extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meetup_id")
    private Long meetupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hobby_id") // hobby 서비스가 생기면 추후 nullable =false 설정
    private Hobby hobby;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "host_id", nullable = false)
    private User hostUser;

    @Column(name = "meetup_name", length = 100)
    private String meetupName;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "location", length = 100)
    private String location;

    @Column(name = "recurrence_rule", length = 50)
    private String recurrenceRule;

    @Column(name = "max_participants")
    private Integer maxParticipants;

    @OneToMany(mappedBy = "meetup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants;

    public void updateMeetupName(String newMeetupName) {
        this.meetupName = newMeetupName;
    }

    public void updateDescription(String newDescription) {
        this.description = newDescription;
    }

    public void updateLocation(String location) {
        this.location = location;
    }

    public void updateRecurrenceRule(String newRecurrenceRule) {
        this.recurrenceRule = newRecurrenceRule;
    }

    public void updateMaxParticipants(int newMaxParticipants) {
        this.maxParticipants = newMaxParticipants;
    }

    public void updateHobby(Hobby newHobby) {
        this.hobby = newHobby;
    }
}
