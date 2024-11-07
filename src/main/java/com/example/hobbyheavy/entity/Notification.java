package com.example.hobbyheavy.entity;

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
public class Notification extends Base {

    // 알림 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    // 모임 고유 ID
    @Column(name = "meetup_id", nullable = true)
    private String meetup;

    // 사용자 고유 ID
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림 유형 (new meetup, nuw join)
    @Column(name = "type", nullable = true)
    private String type;

    // 알림 메세지 내용
    @Column(name = "message", nullable = true)
    private String message;

    // 알람 읽음 여부
    @Column(name = "is_read", nullable = true)
    private Boolean isRead = false;

}
