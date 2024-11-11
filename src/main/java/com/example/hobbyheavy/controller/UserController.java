package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.request.PasswordUpdateRequest;
import com.example.hobbyheavy.dto.response.UserInfoResponse;
import com.example.hobbyheavy.repository.UserRepository;
import com.example.hobbyheavy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    // 나의 회원정보 조회
    @GetMapping("/my-info")
    public ResponseEntity<UserInfoResponse> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {

        // userDetails에서 userId 추출
        String userId = userDetails.getUsername();

        UserInfoResponse userInfo = userService.getMyUserInfo(userId);

        return ResponseEntity.ok(userInfo);
    }

    // 비밀번호 변경
    @PutMapping("/password")
    public ResponseEntity<String> updatePassword(@RequestBody PasswordUpdateRequest request, Authentication authentication) {

        // 현재 인증된 사용자 ID를 가져옴
        String userId = authentication.getName();

        // 비밀번호 변경 수행
        userService.updatePassword(userId, request.getOldPassword(), request.getNewPassword());

        return ResponseEntity.ok("Password changed successfully");
    }

    // 비밀번호 찾기
}