package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ReplyCommandErrorCode implements ErrorCode {
    NOT_FOUND_REPLY(HttpStatus.NOT_FOUND, "NOT_FOUND_REPLY", "답글이 존재하지 않습니다."),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    ReplyCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
