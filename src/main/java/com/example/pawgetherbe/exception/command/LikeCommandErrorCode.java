package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum LikeCommandErrorCode implements ErrorCode {

    NOT_FOUND_LIKE(HttpStatus.NOT_FOUND, "NOT_FOUND_LIKE", "좋아요가 존재하지 않습니다."),
//    NOT_FOUND_TARGET(HttpStatus.NOT_FOUND, "NOT_FOUND_TARGET", "좋아요 대상이 존재하지 않습니다."),
    ALREADY_FOUND_LIKE(HttpStatus.CONFLICT, "ALREADY_FOUND_LIKE", "좋아요가 이미 존재합니다."),
    FAIL_LIKE(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_LIKE", "좋아요 실패"),
    FAIL_CANCEL_LIKE(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_CANCEL_LIKE", "좋아요 취소 실패")
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    LikeCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
