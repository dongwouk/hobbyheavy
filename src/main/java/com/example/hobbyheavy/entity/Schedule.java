package com.example.hobbyheavy.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meetup_id", nullable = false)
    private Meetups meetup;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "votes")
    private Integer votes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vote> votesList;

    public void updateStatus(String status) {
        this.status = status;
    }

    public void updateVotes(Integer votes) {
        this.votes = votes;
    }

    public void updateUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
