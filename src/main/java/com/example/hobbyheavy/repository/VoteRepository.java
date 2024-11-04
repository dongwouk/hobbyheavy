package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // 필요한 경우 추가적인 조회 메서드를 정의할 수 있습니다.
}