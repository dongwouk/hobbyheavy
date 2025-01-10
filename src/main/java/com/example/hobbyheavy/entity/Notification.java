package com.example.hobbyheavy.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification extends Base {

    // 알림 고유 ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long notificationId;

    // 모임과의 관계 설정
    @ManyToOne
    @JoinColumn(name = "meetup_id", nullable = true)
    private Meetup meetup;

    // 사용자 고유 ID
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 알림 유형 (Enum 타입으로 설정)
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private com.example.hobbyheavy.type.Notification type;

    // 알림 메세지 내용
    @Column(name = "message", nullable = false)
    private String message;

    // 알람 읽음 여부 (기본값 false)
    @Column(name = "is_read", nullable = false)
    private Boolean isRead = false;

    // 알림의 읽음 상태를 변경하는 메서드
    public void setIsRead(Boolean isRead) {

        this.isRead = isRead;
    }
}
