package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.UserInfoDTO;
import com.example.hobbyheavy.entity.User;
import com.example.hobbyheavy.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/my-info")
    public ResponseEntity<UserInfoDTO> getMyInfo(@AuthenticationPrincipal UserDetails userDetails) {
        // userDetails에서 userId 추출
        String userId = userDetails.getUsername();

        // 사용자 정보 조회
        User user = userRepository.findByUserId(userId);

        // null 체크
        if (user == null) {
            return ResponseEntity.status(404).body(null); // 사용자 정보를 찾지 못한 경우 404 응답 나중에 에러 핸들링에서 추가/수정
        }

        // 조회된 사용자 정보로 UserInfoDTO를 생성하여 반환
        UserInfoDTO userInfoDTO = new UserInfoDTO().toUserInfoDTO(user);

        return ResponseEntity.ok(userInfoDTO);
    }
}