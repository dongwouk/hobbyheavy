package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 나의 회원정보 조회 메서드
    public UserInfoResponse getMyUserInfo(String userId) {

        // 사용자 조회
        User user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
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
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        // 기존 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }

        // Password 최소 8자 체크
        if (newPassword.length() < 8) {
            throw new CustomException(ExceptionCode.PASSWORD_TOO_SHORT);
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
            throw new CustomException(ExceptionCode.USER_NOT_FOUND);
        }

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }

        // 사용자 삭제
        userRepository.delete(user);
    }

}
