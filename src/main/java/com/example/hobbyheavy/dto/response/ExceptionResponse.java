package com.example.hobbyheavy.dto.response;

import com.example.hobbyheavy.exception.ExceptionCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ExceptionResponse {

    private String message;
    private ExceptionCode exceptionCode;
}
