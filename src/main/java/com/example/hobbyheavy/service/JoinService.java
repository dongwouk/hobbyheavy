package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.JoinResponse;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.Role;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 유효성 체크 메서드
    void checkJoin(JoinResponse joinResponse) {

        // UserId 중복 체크
        if(userRepository.existsByUserId(joinResponse.getUserId())) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        // email 중복 체크
        if (userRepository.existsByEmail(joinResponse.getEmail())) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

    }

    // 회원 가입 메서드
    @Transactional
    public void joinProcess(@Valid JoinResponse joinResponse) {

        // 유효성 체크
        checkJoin(joinResponse);

        // 사용자 저장
        userRepository.save(User.builder()
                .userId(joinResponse.getUserId())
                .username(joinResponse.getUsername())
                .password(bCryptPasswordEncoder.encode(joinResponse.getPassword())) // 암호화된 비밀번호
                .email(joinResponse.getEmail())
                .gender(joinResponse.getGender())
                .age(joinResponse.getAge())
                .hobby(joinResponse.getHobby())
                .role(Collections.singleton(Role.ROLE_USER)) // 역할이 존재할 때 설정
                .build());
    }

}
