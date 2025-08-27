package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PetFairQueryErrorCode implements ErrorCode {
    NOT_FOUND_PET_FAIR_POSTER(HttpStatus.NOT_FOUND, "NOT_FOUND_PET_FAIR_POSTER", "펫페어 포스터가 없습니다."),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    PetFairQueryErrorCode(HttpStatus httpStatus, String code, String message) {
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
