package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.UserNotFoundException;
import com.example.hobbyheavy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 나의 회원정보 조회 메서드
    public UserInfoResponse getMyUserInfo(String userId) {
        // 사용자 정보 조회
        User user = userRepository.findByUserId(userId);

        // null 체크
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // 조회된 사용자 정보로 UserInfoDTO 생성 후 리턴
        return new UserInfoResponse().toUserInfoDTO(user);

    }

    // 비밀번호 변경 메서드
    @Transactional
    public void updatePassword(String userId, String oldPassword, String newPassword) {

        // 사용자 조회
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        // 기존 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("기존 비밀번호가 일치하지 않습니다.");
        }

        // Password 최소 8자 체크
        if (newPassword.length() < 8) {
            throw new IllegalArgumentException("새 비밀번호는 최소 8자 이상이어야 합니다.");
        }

        // 새 비밀번호로 변경
        user.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // 회원 탈퇴 메서드
    @Transactional
    public void deleteUser(String userId, String password) throws Exception {
        // 사용자 조회
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        // 사용자 삭제
        userRepository.delete(user);
    }

}
