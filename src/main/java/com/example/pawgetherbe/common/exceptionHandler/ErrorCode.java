package com.example.pawgetherbe.common.exceptionHandler;

import org.springframework.http.HttpStatus;

public interface ErrorCode {
    String code();
    String message();
    HttpStatus httpStatus();
}
