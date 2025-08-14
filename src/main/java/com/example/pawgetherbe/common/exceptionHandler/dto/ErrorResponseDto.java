package com.example.pawgetherbe.common.exceptionHandler.dto;

import lombok.Builder;

@Builder
public record ErrorResponseDto (
        int status,
        String code,
        String message
) {}
