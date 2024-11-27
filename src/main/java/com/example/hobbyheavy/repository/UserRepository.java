package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Boolean existsByUserId(String userId); // userId 중복

    Boolean existsByEmail(String email); // email 중복

    Optional<User> findByUserId(String userId); // userId 로 찾기

    Optional<User> findByUserIdAndDeletedFalse(String userId); // userId 로 탈퇴하지 않은 사용자 찾기

    Optional<User> findByEmailAndDeletedFalse(String userId); // email 로 탈퇴하지 않은 사용자 찾기

    boolean existsByUserIdAndDeletedFalse(String userId); // 현재 활성화된 아이디인지 체크

    boolean existsByEmailAndDeletedFalse(String email); // 현재 활성화된 이미일인지 체크

    Optional<User> findByUserIdAndDeletedTrue(String userId); // 현재 탈퇴된 아이디인지 체크

    Optional<User> findByEmailAndDeletedTrue(String email); // 현재 탈퇴된 이메일인지 체크

}
