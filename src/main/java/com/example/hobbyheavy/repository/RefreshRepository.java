package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Refresh;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    // refresh token 존재여부 확인
    Boolean existsByRefresh(String refresh);

    // refresh token delete 메서드
    // DB에 적용하므로 transactional 어노테이션 설정
    @Transactional
    void deleteByRefresh(String refresh);
}
