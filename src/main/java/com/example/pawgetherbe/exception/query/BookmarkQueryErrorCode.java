package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkQueryErrorCode implements ErrorCode {
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "NOT_FOUND_BOOKMARK", "북마크가 존재하지 않습니다.");

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    BookmarkQueryErrorCode(HttpStatus httpStatus, String code, String message) {
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
