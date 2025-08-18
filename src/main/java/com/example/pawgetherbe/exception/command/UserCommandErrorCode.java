package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum UserCommandErrorCode implements ErrorCode {
    UNAUTHORIZED_PASSWORD(HttpStatus.UNAUTHORIZED,"UNAUTHORIZED_PASSWORD","아이디 또는 비밀번호가 올바르지 않습니다."),
    UNAUTHORIZED_LOGIN(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_LOGIN", "다시 로그인을 진행해주세요."),

    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"NOT_FOUND_USER","존재하지 않는 계정입니다."),

    OAUTH_PROVIDER_NOT_SUPPORTED(HttpStatus.BAD_REQUEST, "OAUTH_PROVIDER_NOT_SUPPORTED", "지원하지 않는 OAuth2 입니다."),

    CONFLICT_EMAIL(HttpStatus.CONFLICT, "CONFLICT_EMAIL", "이미 존재하는 Email 입니다."),
    CONFLICT_NICKNAME(HttpStatus.CONFLICT, "CONFLICT_NICKNAME", "이미 존재하는 NickName 입니다."),
    CONFLICT_USER(HttpStatus.CONFLICT, "CONFLICT_USER", "이미 가입된 계정입니다")
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
