package com.example.pawgetherbe.exception.query;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookmarkQueryErrorCode implements ErrorCode {
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "NOT_FOUND_BOOKMARK", "북마크가 존재하지 않습니다."),
    FAIL_READ_BOOKMARK_LIST(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_READ_BOOKMARK_LIST", "북마크 리스트 불러오기 실패"),
    FAIL_READ_BOOKMARK_STATUS(HttpStatus.INTERNAL_SERVER_ERROR, "FAIL_READ_BOOKMARK_STATUS", "북마크 상태 불러오기 실패"),
    NOT_FOUND_SOME_TARGET(HttpStatus.NOT_FOUND, "NOT_FOUND_SOME_TARGET", "북마크 게시글 대상의 일부가 존재하지 않습니다."),
    OVER_SIZE_TARGET_IDS(HttpStatus.BAD_REQUEST, "OVER_SIZE_TARGET_IDS", "요청한 게시글 아이디 개수를 확인해 주세요.");

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
