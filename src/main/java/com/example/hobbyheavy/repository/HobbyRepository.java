package com.example.hobbyheavy.repository;

import com.example.hobbyheavy.entity.Hobby;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HobbyRepository extends JpaRepository<Hobby, Long> {

    Optional<Hobby> findFirstByHobbyName(String hobbyName);
}
