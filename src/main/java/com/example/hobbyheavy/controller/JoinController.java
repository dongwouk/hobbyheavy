package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.JoinDTO;
import com.example.hobbyheavy.service.JoinService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class JoinController {

    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);
    private final JoinService joinService;

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(@Valid @RequestBody JoinDTO joinDTO) {
        logger.info("회원가입 요청: {}", joinDTO);
        try {
            joinService.joinProcess(joinDTO);
            logger.info("회원가입 처리 완료: {}", joinDTO.getUserId());
            return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공");
        } catch (IllegalArgumentException e) {
            logger.error("회원가입 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 중 오류가 발생했습니다.");
        }
    }
}
