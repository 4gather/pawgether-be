package com.example.pawgetherbe.common.exception;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserCommandErrorCode implements ErrorCode {
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"INVALID_PASSWORD","아이디 또는 비밀번호가 올바르지 않습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"NOT_FOUND_USER","존재하지 않는 계정입니다."),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATE_EMAIL", "email 형식을 지켜주세요"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "DUPLICATE_NICKNAME", "nickname 은 형식을 지켜주세요"),
    CONFLICT_EMAIL(HttpStatus.CONFLICT, "CONFLICT_EMAIL", "이미 존재하는 Email 입니다."),
    CONFLICT_NICKNAME(HttpStatus.CONFLICT, "CONFLICT_NICKNAME", "이미 존재하는 NickName 입니다.")
    ;

    private final String message;
    private final HttpStatus httpStatus;
    private final String code;

    UserCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
