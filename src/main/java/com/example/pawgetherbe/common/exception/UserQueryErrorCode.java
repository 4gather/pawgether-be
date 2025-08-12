package com.example.pawgetherbe.common.exception;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserQueryErrorCode implements ErrorCode {
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    UserQueryErrorCode(HttpStatus httpStatus, String code, String message) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.code = code;
    }

    @Override
    public String code() {
        return code;
    }

    @Override
    public String message() {
        return message;
    }

    @Override
    public HttpStatus httpStatus() {
        return httpStatus;
    }
}
