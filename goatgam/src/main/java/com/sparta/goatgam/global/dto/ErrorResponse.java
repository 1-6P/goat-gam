package com.sparta.goatgam.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.sparta.goatgam.global.exception.ExceptionCode;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String code, String message) {
    public static ResponseEntity<ErrorResponse> errorResponse(ExceptionCode exceptionCode) {
        return ResponseEntity
                .status(exceptionCode.getStatus())
                .body(new ErrorResponse(exceptionCode.getCode(), exceptionCode.getMessage()));
    }
}
