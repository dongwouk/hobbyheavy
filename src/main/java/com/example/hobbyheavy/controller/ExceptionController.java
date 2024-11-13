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

@ControllerAdvice
@Slf4j
public class ExceptionController {

    /*
    CustomException 발생
    ex) 401 Unauthorized
    {"message": "인증된 사용자가 아닙니다.",
     "exceptionCode": "UNAUTHORIZED_USER"}
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ExceptionResponse> handleCustomException(CustomException e) {
        ExceptionCode exceptionCode = e.getExceptionCode();
        log.warn(exceptionCode.getMessage(), exceptionCode);
        return ResponseEntity
                .status(exceptionCode.getHttpStatus())
                .body(new ExceptionResponse(exceptionCode.getMessage(), exceptionCode));
    }

    /*
    입력 값 검증 실패
    {"status": 400,
     "error": "User ID is required, Email must be a valid email address"}
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String errorMessages = e.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn(errorMessages);
        return ResponseEntity
                .badRequest().body(errorMessages);
    }
}
