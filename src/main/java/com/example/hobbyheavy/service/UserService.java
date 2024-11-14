package com.example.hobbyheavy.service;

import com.example.hobbyheavy.dto.request.JoinRequest;
import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.type.Role;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    /** 회원가입 유효성 체크 메서드 **/
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

    /** 사용자 조회 공통 메서드 **/
    private User getUser(String userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> {
                    log.warn("사용자 조회 실패. 입력한 사용자 ID: {}", userId);
                    return new CustomException(ExceptionCode.USER_NOT_FOUND);
                });
    }

    /** 비밀번호 확인 공통 메서드 **/
    private void checkPassword(String userId, String password, String storedPassword) {
        if (!bCryptPasswordEncoder.matches(password, storedPassword)) {
            log.warn("비밀번호 불일치. 사용자 ID: {}", userId);
            throw new CustomException(ExceptionCode.PASSWORD_MISMATCH);
        }
    }

    /** ===비즈니스 로직=== **/

    /** 회원 가입 메서드 **/
    public void JoinUser(JoinRequest joinRequest) {

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
        checkPassword(userId, oldPassword, user.getPassword());

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
    public void deleteUser(String userId, String password) {
        // 사용자 조회
        User user = getUser(userId);

        // 비밀번호 확인
        checkPassword(userId, password, user.getPassword());

        // 사용자 삭제
        userRepository.delete(user);
        log.info("사용자 ID: {}의 계정이 성공적으로 탈퇴 처리 되었습니다.", userId);
    }

}
