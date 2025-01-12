package com.example.hobbyheavy.controller;

import com.example.hobbyheavy.dto.response.ExceptionResponse;
import com.example.hobbyheavy.exception.CustomException;
import com.example.hobbyheavy.exception.ExceptionCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 컨트롤러
 * 모든 컨트롤러에서 발생하는 예외를 처리하고, 사용자에게 적절한 응답을 반환.
 */
@ControllerAdvice // 컨트롤러 전역에서 발생하는 예외를 처리
@Slf4j // 로깅을 위한 Lombok 어노테이션
public class ExceptionController {

    /**
     * CustomException 예외 처리
     * 사용자 정의 예외가 발생했을 때 HTTP 상태 코드와 메시지를 포함한 응답을 반환.
     *
     * @param e CustomException 객체
     * @return 예외 메시지와 코드가 포함된 응답
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        // CustomException에서 가져온 ExceptionCode를 통해 메시지와 상태 코드 생성
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.warn(exceptionCode.getMessage(), exceptionCode); // 예외 로그 경고 레벨로 기록
        return ResponseEntity
                .status(exceptionCode.getHttpStatus()) // HTTP 상태 코드 설정
                .body(new ExceptionResponse(exceptionCode.getMessage(), exceptionCode)); // 예외 응답 객체 반환
    }

    /**
     * 입력 값 검증 실패 예외 처리
     * 요청 DTO에서 유효성 검사가 실패한 경우 400 상태 코드와 에러 메시지를 반환.
     *
     * @param e MethodArgumentNotValidException 객체
     * @return 검증 실패 메시지 목록
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 모든 검증 실패 메시지를 문자열로 합치기
        String errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage) // 검증 실패 메시지 추출
                .collect(Collectors.joining(", ")); // 메시지를 콤마로 구분하여 병합
        log.warn(errorMessages); // 검증 실패 메시지 로그로 기록
        return ResponseEntity
                .badRequest() // HTTP 상태 400 반환
                .body(errorMessages); // 에러 메시지 반환
    }
}
