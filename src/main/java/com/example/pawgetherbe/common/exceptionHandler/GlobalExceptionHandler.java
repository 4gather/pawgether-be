package com.example.pawgetherbe.common.exceptionHandler;

import com.example.pawgetherbe.common.exceptionHandler.dto.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public final class GlobalExceptionHandler {
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponseDto> handleCustomException(CustomException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        var responseBody = ErrorResponseDto.builder()
                .status(errorCode.httpStatus().value())
                .code(errorCode.code())
                .message(exception.getMessage())
                .build();

        return ResponseEntity
                .status(errorCode.httpStatus())
                .body(responseBody);
    }
}
