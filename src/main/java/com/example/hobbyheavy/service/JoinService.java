package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.JoinDTO;
import com.example.hobbyheavy.entity.UserEntity;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class JoinService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Transactional
    public void joinProcess(JoinDTO joinDTO) {

        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();

        // Username 중복 체크
        if(userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        // password 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // UserEntity 생성
        UserEntity data = UserEntity.builder()
                .username(username)
                .password(encodedPassword)
                .email(email)
                .role(Collections.singleton(Role.ROLE_USER)) // 역할이 존재할 때 설정
                .build();

        // 사용자 저장
        userRepository.save(data);
    }
}
