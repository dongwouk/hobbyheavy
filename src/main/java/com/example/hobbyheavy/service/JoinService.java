package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.JoinDTO;
import com.example.hobbyheavy.entity.Hobby;
import com.example.hobbyheavy.entity.User;
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

        String userId = joinDTO.getUserId(); // 로그인 ID는 userId로 사용
        String username = joinDTO.getUsername(); // 사용자의 이름
        String password = joinDTO.getPassword();
        String email = joinDTO.getEmail();
        Boolean gender = joinDTO.getGender();
        Integer age = joinDTO.getAge();
        Hobby hobby = joinDTO.getHobby();

        // UserId 중복 체크
        if(userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("중복된 아이디입니다.");
        }

        // email 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("중복된 이메일입니다.");
        }

        // password 암호화
        String encodedPassword = bCryptPasswordEncoder.encode(password);

        // UserEntity 생성
        User data = User.builder()
                .userId(userId)
                .username(username)
                .password(encodedPassword)
                .email(email)
                .gender(gender)
                .age(age)
                .hobby(hobby)
                .role(Collections.singleton(Role.ROLE_USER)) // 역할이 존재할 때 설정
                .build();

        // 사용자 저장
        userRepository.save(data);
    }
}
