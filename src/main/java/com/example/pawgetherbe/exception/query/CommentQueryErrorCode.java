package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CommentQueryErrorCode implements ErrorCode {
    NOT_FOUND_COMMENT_CALENDAR(HttpStatus.NOT_FOUND, "NOT_FOUND_COMMENT_CALENDAR", "댓글이 존재하지 않습니다."),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    CommentQueryErrorCode(HttpStatus httpStatus, String code, String message) {
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
