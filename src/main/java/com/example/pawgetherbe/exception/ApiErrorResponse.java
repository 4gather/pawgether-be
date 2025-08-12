package com.example.pawgetherbe.exception;

public record ApiErrorResponse(
        int statusValue,
        String code, // 추후에 ErrorCode 회의 시 Enum 으로 변경 가능성 있음
        String message
) {
}
