package com.example.pawgetherbe.exception.command;

import com.example.pawgetherbe.common.exceptionHandler.ErrorCode;
import org.springframework.http.HttpStatus;

public enum CommentCommandErrorCode implements ErrorCode {
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "NOT_FOUND_COMMENT", "댓글이 존재하지 않습니다."),
    CREATE_INTERNAL_COMMENT(HttpStatus.INTERNAL_SERVER_ERROR, "CREATE_INTERNAL_COMMENT", "댓글 생성중 오류가 발생했습니다."),
    DELETE_CONFLICT_COMMENT(HttpStatus.CONFLICT, "DELETE_CONFLICT_COMMENT", "이미 삭제된 댓글입니다."),
    ;

    private final String message;
    private final String code;
    private final HttpStatus httpStatus;

    CommentCommandErrorCode(HttpStatus httpStatus, String code, String message) {
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
