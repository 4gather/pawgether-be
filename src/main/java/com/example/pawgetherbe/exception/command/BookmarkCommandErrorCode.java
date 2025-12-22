package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkCommandErrorCode implements ErrorCode {
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "NOT_FOUND_BOOKMARK", "북마크가 존재하지 않습니다."),
    ALREADY_BOOKMARK(HttpStatus.CONFLICT, "ALREADY_BOOKMARK", "이미 북마크가 등록되어 있습니다."),
    FAIL_CANCEL_BOOKMARK(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_CANCEL_BOOKMARK", "북마크 취소 실패"),
    FAIL_CREATE_BOOKMARK(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_CREATE_BOOKMARK", "북마크 생성 실패");

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    BookmarkCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
