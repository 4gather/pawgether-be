package com.example.pawgetherbe.common.exceptionHandler;

import com.example.pawgetherbe.common.exceptionHandler.dto.ErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;
import java.util.stream.Collectors;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        // 필드 에러 메시지 추출
        List<String> errorMessages = fieldErrors.stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        var responseBody = ErrorResponseDto.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .code("VALIDATION_FAILED")
                .message(String.valueOf(errorMessages))
                .build();

        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDto> handleMaxUploadSize(MaxUploadSizeExceededException ex) {
        var body = ErrorResponseDto.builder()
                .status(HttpStatus.PAYLOAD_TOO_LARGE.value())
                .code("FILE_TOO_LARGE")
                .message("업로드 가능한 파일 용량을 초과했습니다.")
                .build();
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(body);
    }
}
