package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.JoinRequest;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.Role;
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
    void checkJoin(JoinRequest joinRequest) {

        // UserId 중복 체크
        if(userRepository.existsByUserId(joinRequest.getUserId())) {
            throw new CustomException(ExceptionCode.USER_ID_ALREADY_IN_USE);
        }

        // email 중복 체크
        if (userRepository.existsByEmail(joinRequest.getEmail())) {
            throw new CustomException(ExceptionCode.EMAIL_ALREADY_IN_USE);
        }

    }

    // 회원 가입 메서드
    public void joinProcess(JoinRequest joinRequest) {

        // 유효성 체크
        checkJoin(joinRequest);

        // 사용자 저장
        userRepository.save(User.builder()
                .userId(joinRequest.getUserId())
                .username(joinRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(joinRequest.getPassword())) // 암호화된 비밀번호
                .email(joinRequest.getEmail())
                .gender(joinRequest.getGender())
                .age(joinRequest.getAge())
                .hobby(joinRequest.getHobby())
                .role(Collections.singleton(Role.ROLE_USER)) // 역할이 존재할 때 설정
                .build());
    }

}
