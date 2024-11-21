package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    /**
     * 특정 사용자 ID와 관련된 모든 알림을 조회합니다.
     *
     * @param userId 사용자 ID
     * @return 해당 사용자가 받은 알림 목록
     */
    List<Notification> findAllByUser_UserId(String userId);
}