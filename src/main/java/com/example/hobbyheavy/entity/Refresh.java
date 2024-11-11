package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Refresh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String refresh;

    private LocalDateTime expiration;

    public void updateRefresh(String refresh, LocalDateTime expiration) {
        this.refresh = refresh;
        this.expiration = expiration;
    }
}
