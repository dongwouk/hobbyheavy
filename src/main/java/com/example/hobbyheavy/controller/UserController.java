package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.auth.CustomUserDetails;
import com.example.hobbyheavy.dto.request.*;
import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import com.example.hobbyheavy.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 회원가입 **/
    @PostMapping
    public ResponseEntity<String> joinUser(@Valid @RequestBody UserJoinRequest userJoinRequest) {
        log.info("회원가입 요청: {}", userJoinRequest);
        try {
            userService.JoinUser(userJoinRequest);
            log.info("회원가입 처리 완료: {}", userJoinRequest.getUserId());
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body("회원가입 성공");
        } catch (CustomException e) {
            log.error("회원가입 중 오류 발생: {}", e.getMessage());
            return ResponseEntity
                    .status(e.getExceptionCode().getHttpStatus())
                    .body(e.getMessage());
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("회원가입 중 오류가 발생했습니다.");
        }
    }

    /** 나의 회원정보 조회 **/
    @GetMapping("/me")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {

        // userDetails에서 userId 추출
        String userId = userDetails.getUsername();

        UserInfoResponse userInfo = userService.getMyUserInfo(userId);

        return ResponseEntity.ok(userInfo);
    }

    /** 나의 회원정보 변경 **/
    @PutMapping("/me")
    public ResponseEntity<String> updateUserInfo(@Valid @RequestBody UserUpdateRequest request, Authentication authentication) {

        // 현재 인증된 사용자 ID를 가져옴
        String userId = authentication.getName();
        log.info("회원정보 변경 요청. userId: {}, 변경할 username: {}, 변경할 hobbyIds: {}", userId, request.getUsername(), request.getHobbyIds());

        try {
            // hobbyIds 가 null 인 경우 빈 리스트로 처리
            if (request.getHobbyIds() == null) {
                request.setHobbyIds(new HashSet<>());
            }

            // 유저 정보 업데이트
            userService.updateUserInfo(userId, request);
            log.info("회원정보 변경 성공. userId: {}", userId);

            // 성공 응답 반환
            return ResponseEntity.ok("User information updated successfully");

        } catch (CustomException e) {
            log.error("회원정보 변경 실패. userId: {}, 오류: {}", userId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update user information.");
        }
    }

    /** 비밀번호 변경 **/
    @PutMapping("/me/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody UserPwUpdateRequest request, Authentication authentication) {

        // 현재 인증된 사용자 ID를 가져옴
        String userId = authentication.getName();

        // 비밀번호 변경 수행
        userService.updatePassword(userId, request);

        return ResponseEntity.ok("패스워드 변경 성공.");
    }

    /** 비밀번호 찾기 **/

    /** 회원 탈퇴 **/
    @DeleteMapping("/me")
    public ResponseEntity<String> deleteUser(@Valid @RequestBody UserDeleteRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String password = request.getPassword();

        try {
            userService.deleteUser(userDetails.getUsername(), password); // 사용자 삭제
            return ResponseEntity.ok("회원 탈퇴 성공.");
        } catch (Exception e) {
            throw new CustomException(ExceptionCode.USER_DELETE_FAILED);
        }
    }
}