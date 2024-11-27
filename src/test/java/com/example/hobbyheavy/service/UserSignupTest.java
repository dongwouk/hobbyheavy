package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.type.UserRole;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserSignupTest {

    private PasswordEncoder passwordEncoder;

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void userSignupSuccess() {
        // Given: 새로운 유저 정보를 생성한다.
        User user = User.builder()
                .userId("testUser123")
                .username("Test User")
                .password(passwordEncoder.encode("password123"))
                .email("testuser@example.com")
                .gender(true) // true: 남성, false: 여성
                .age(25)
                .userRole(Set.of(UserRole.ROLE_USER))
                .build();

        // When: 비밀번호가 암호화되었는지 확인한다.
        boolean isPasswordMatch = passwordEncoder.matches("password123", user.getPassword());

        // Then: 유저의 정보가 올바르게 설정되었는지 검증한다.
        assertNotNull(user.getUserId(), "User ID는 null이 아니어야 합니다.");
        assertEquals("Test User", user.getUsername(), "사용자 이름이 일치해야 합니다.");
        assertEquals("testuser@example.com", user.getEmail(), "이메일이 일치해야 합니다.");
        assertTrue(isPasswordMatch, "비밀번호가 올바르게 암호화되어야 합니다.");
        assertEquals(25, user.getAge(), "나이가 일치해야 합니다.");
        assertTrue(user.getUserRole().contains(UserRole.ROLE_USER), "유저의 역할이 ROLE_USER여야 합니다.");
    }


}
