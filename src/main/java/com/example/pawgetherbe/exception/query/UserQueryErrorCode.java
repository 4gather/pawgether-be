package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserQueryErrorCode implements ErrorCode {
    INVALID_FORMAT_EMAIL(HttpStatus.BAD_REQUEST, "INVALID_FORMAT_EMAIL", "email 형식을 지켜주세요"),
    INVALID_FORMAT_NICKNAME(HttpStatus.BAD_REQUEST, "INVALID_FORMAT_NICKNAME", "nickname 은 형식을 지켜주세요")
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
