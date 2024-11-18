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

}
