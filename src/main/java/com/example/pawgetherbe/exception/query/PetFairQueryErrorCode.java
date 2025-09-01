package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PetFairQueryErrorCode implements ErrorCode {
    NOT_FOUND_PET_FAIR_POSTER(HttpStatus.NOT_FOUND, "NOT_FOUND_PET_FAIR_POSTER", "펫페어 포스터가 없습니다."),
    NOT_FOUND_PET_FAIR_CALENDAR(HttpStatus.NOT_FOUND, "NOT_FOUND_PET_FAIR_CALENDAR", "펫페어 행사가 없습니다."),
    NOT_FOUND_PET_FAIR_POST(HttpStatus.NOT_FOUND, "NOT_FOUND_PET_FAIR_POST", "게시글이 존재하지 않습니다."),
    EMPTY_PET_FAIR_FILTER_STATUS(HttpStatus.BAD_REQUEST, "EMPTY_PET_FAIR_FILTER_STATUS", "필터 상태를 입력해주세요."),
    INVALID_PET_FAIR_FILTER_STATUS(HttpStatus.BAD_REQUEST, "INVALID_PET_FAIR_FILTER_STATUS", "올바른 필터 상태를 입력해주세요.")
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
