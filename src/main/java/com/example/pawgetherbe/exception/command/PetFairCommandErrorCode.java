package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PetFairCommandErrorCode implements ErrorCode {
    PET_FAIR_CREATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "CREATE_FAIL", "펫페어 생성 실패"),
    IMAGE_CONVERT_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_CONVERT_FAIL", "이미지를 webp로 변환하는 중 오류가 발생했습니다."),
    REMOVED_PET_FAIR(HttpStatus.CONFLICT, "REMOVED_PET_FAIR", "이미 삭제된 게시글입니다."),
    NOT_FOUND_PET_FAIR(HttpStatus.NOT_FOUND, "NOT_FOUND_PET_FAIR", "해당 펫페어가 없습니다."),
    UNAUTHORIZED_UPDATE_PET_FAIR(HttpStatus.UNAUTHORIZED, "UNAUTHORIZED_UPDATE_PET_FAIR", "해당 게시글을 수정할 수 없습니다."),
    EMPTY_ANY_FIELD(HttpStatus.BAD_REQUEST, "EMPTY_ANY_FIELD", "비어있는 값을 입력해주세요."),
    PET_FAIR_UPDATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "UPDATE_FAIL", "펫페어 업데이트 실패"),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    PetFairCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
