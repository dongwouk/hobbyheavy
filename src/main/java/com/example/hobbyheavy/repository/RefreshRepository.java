package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    // refresh token 존재여부 확인
    Boolean existsByRefresh(String refresh);

    // 특정 userId에 대한 refresh token 존재 여부 확인
    Boolean existsByUserId(String userId);

    // 특정 userId의 refresh token 가져오기
    Optional<Refresh> findByUserId(String userId);

    // refresh token delete 메서드
    // DB에 적용하므로 transactional 어노테이션 설정
    @Transactional
    void deleteByRefresh(String refresh);

    // 만료된 토큰을 전부 제거하는 메서드
    @Transactional
    void deleteAllByExpirationBefore(LocalDateTime expirationDate);
}
