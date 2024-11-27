package com.example.hobbyheavy.service;

import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.type.UserRole;
import jakarta.validation.ConstraintViolation;
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
public class UserSignupValidationTest {

    private PasswordEncoder passwordEncoder;

    @Autowired
    private Validator validator;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
    }

    @Test
    void invalidEmailFormat() {
        // Given: 잘못된 이메일 형식을 가진 유저를 생성한다.
        User userWithInvalidEmail = User.builder()
                .userId("testUser123")
                .username("Test User")
                .password(passwordEncoder.encode("password123"))
                .email("invalid-email-format") // 이메일 형식 오류
                .gender(true)
                .age(25)
                .userRole(Set.of(UserRole.ROLE_USER))
                .build();

        // When: 유효성 검사를 수행한다.
        Set<ConstraintViolation<User>> violations = validator.validate(userWithInvalidEmail);

        // Then: 이메일 형식 오류에 대한 유효성 검사 실패 확인
        assertFalse(violations.isEmpty(), "유효성 검사 위반이 발생해야 합니다.");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email") && v.getMessage().contains("must be a well-formed email address")), "잘못된 이메일 형식인 경우 오류 메시지가 반환되어야 합니다.");
    }

    @Test
    void passwordTooShort() {
        // Given: 너무 짧은 비밀번호를 가진 유저를 생성한다.
        User userWithShortPassword = User.builder()
                .userId("testUser123")
                .username("Test User")
                .password("123") // 비밀번호 길이 오류
                .email("testuser@example.com")
                .gender(true)
                .age(25)
                .userRole(Set.of(UserRole.ROLE_USER))
                .build();

        // When: 유효성 검사를 수행한다.
        Set<ConstraintViolation<User>> violations = validator.validate(userWithShortPassword);

        // Then: 비밀번호 길이에 대한 유효성 검사 실패 확인
        assertFalse(violations.isEmpty(), "유효성 검사 위반이 발생해야 합니다.");
    }

    @Test
    void missingRequiredFields() {
        // Given: 필수 필드가 누락된 유저 정보를 생성한다.
        User userWithMissingFields = User.builder()
                .userId(null)  // 사용자 ID 누락
                .username(null)  // 사용자 이름 누락
                .password(null)  // 비밀번호 누락
                .email(null)     // 이메일 누락
                .gender(null)    // 성별 누락
                .age(null)       // 나이 누락
                .userRole(null)      // 역할 누락
                .build();

        // When: 유효성 검사를 수행한다.
        Set<ConstraintViolation<User>> violations = validator.validate(userWithMissingFields);

        // Then: 필수 필드가 누락되었을 때 적절한 오류 메시지가 반환되는지 검증한다.
        assertFalse(violations.isEmpty(), "유효성 검사 위반이 발생해야 합니다.");
    }

    @Test
    void usernameTooLong() {
        // Given: 너무 긴 사용자 이름을 가진 유저를 생성한다.
        User userWithLongUsername = User.builder()
                .userId("testUser123")
                .username("ThisIsAVeryLongUsernameThatExceedsTheMaximumAllowedLength")
                .password(passwordEncoder.encode("password123"))
                .email("testuser@example.com")
                .gender(true)
                .age(25)
                .userRole(Set.of(UserRole.ROLE_USER))
                .build();

        // When: 유효성 검사를 수행한다.
        Set<ConstraintViolation<User>> violations = validator.validate(userWithLongUsername);

        // Then: 사용자 이름 길이에 대한 유효성 검사 실패 확인
        assertFalse(violations.isEmpty(), "유효성 검사 위반이 발생해야 합니다.");
    }

    @Test
    void ageOutOfBounds() {
        // Given: 잘못된 나이를 가진 유저를 생성한다.
        User userWithInvalidAge = User.builder()
                .userId("testUser123")
                .username("Test User")
                .password(passwordEncoder.encode("password123"))
                .email("testuser@example.com")
                .gender(true)
                .age(150) // 유효하지 않은 나이
                .userRole(Set.of(UserRole.ROLE_USER))
                .build();

        // When: 유효성 검사를 수행한다.
        Set<ConstraintViolation<User>> violations = validator.validate(userWithInvalidAge);

        // Then: 나이에 대한 유효성 검사 실패 확인
        assertFalse(violations.isEmpty(), "유효성 검사 위반이 발생해야 합니다.");
    }
}
