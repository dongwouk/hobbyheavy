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

    /** 사용자 조회 공통 메서드 **/
    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패. 입력한 사용자 ID: {}", userId);
                    return new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
    }

    /** 나의 회원정보 조회 메서드 **/
    public UserInfoResponse getMyUserInfo(String userId) {

        // 사용자 조회
        User user = getUser(userId);
        log.info("사용자 조회 성공. 사용자 ID: {}", userId);

        // 조회된 사용자 정보로 UserInfoDTO 생성 후 리턴
        return new UserInfoResponse().toUserInfoDTO(user);

    }

    /** 비밀번호 변경 메서드 **/
    @Transactional
    public void updatePassword(String userId, String oldPassword, String newPassword) {

        // 사용자 조회
        User user = getUser(userId);

        // 기존 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            log.warn("기존 비밀번호 불일치. 사용자 ID: {}", userId);
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }

        // 기존 비밀번호와 새 비밀번호가 같을 경우 예외 처리
        if (bCryptPasswordEncoder.matches(newPassword, user.getPassword())) {
            log.warn("새 비밀번호가 기존 비밀번호와 동일합니다. 사용자 ID: {}", userId);
            throw new CustomException(ExceptionCode.PASSWORD_SAME_AS_OLD);
        }

        // 새 비밀번호로 변경
        user.updatePassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("사용자 ID: {}의 비밀번호가 성공적으로 변경되었습니다.", userId);
    }

    /** 회원 탈퇴 메서드 **/
    @Transactional
    public void deleteUser(String userId, String password) throws Exception {
        // 사용자 조회
        User user = getUser(userId);

        // 비밀번호 확인
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            log.warn("비밀번호 불일치. 사용자 ID: {}", userId);
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }

        // 사용자 삭제
        userRepository.delete(user);
        log.info("사용자 ID: {}의 계정이 성공적으로 탈퇴 처리 되었습니다.", userId);
    }

}
