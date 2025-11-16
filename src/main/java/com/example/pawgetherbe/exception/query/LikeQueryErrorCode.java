package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LikeQueryErrorCode implements ErrorCode {
    NOT_FOUND_TARGET(HttpStatus.NOT_FOUND, "NOT_FOUND_TARGET", "좋아요 대상이 존재하지 않습니다."),
    NOT_FOUND_SOME_TARGET(HttpStatus.NOT_FOUND, "NOT_FOUND_SOME_TARGET", "좋아요 대상의 일부가 존재하지 않습니다."),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    LikeQueryErrorCode(HttpStatus httpStatus, String code, String message) {
        this.message = message;
        this.code = code;
        this.httpStatus = httpStatus;
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
