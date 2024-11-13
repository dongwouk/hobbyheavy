package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.CustomUserDetails;
import com.example.hobbyheavy.dto.request.PasswordUpdateRequest;
import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** 나의 회원정보 조회 **/
    @GetMapping("/my-info")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {

        // userDetails에서 userId 추출
        String userId = userDetails.getUsername();

        UserInfoResponse userInfo = userService.getMyUserInfo(userId);

        return ResponseEntity.ok(userInfo);
    }

    /** 비밀번호 변경 **/
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest request, Authentication authentication) {

        // 현재 인증된 사용자 ID를 가져옴
        String userId = authentication.getName();

        // 비밀번호 변경 수행
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());

        return ResponseEntity.ok("패스워드 변경 성공.");
    }

    /* 비밀번호 찾기 **/

    /** 회원 탈퇴 **/
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteUser(@RequestBody Map<String, String> request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String password = request.get("password");

        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required.");
        }

        try {
            userService.deleteUser(userDetails.getUsername(), password); // 사용자 삭제
            return ResponseEntity.ok("User deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user.");
        }
    }
}