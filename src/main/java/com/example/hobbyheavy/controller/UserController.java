package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.UserInfoDTO;
import com.example.hobbyheavy.entity.UserEntity;
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
        // userDetails에서 username 추출
        String username = userDetails.getUsername();

        // 사용자 정보 조회
        UserEntity user = userRepository.findByUsername(username);

        // 조회된 사용자 정보로 UserInfoDTO를 생성하여 반환
        UserInfoDTO userInfoDTO = UserInfoDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedDate())
                .build();

        return ResponseEntity.ok(userInfoDTO);
    }
}