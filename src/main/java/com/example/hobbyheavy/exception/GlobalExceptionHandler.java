package com.example.hobbyheavy.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // CustomException을 처리하는 핸들러
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<String> handleCustomException(CustomException ex) {
        log.error("CustomException 발생: {}", ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getMessage());
    }

    // 유효성 검증 실패 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("유효성 검증 오류 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효성 검증 오류가 발생했습니다.");
    }

    // 그 외의 모든 예외를 처리하는 핸들러
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        log.error("예기치 못한 예외 발생: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
    }
}
