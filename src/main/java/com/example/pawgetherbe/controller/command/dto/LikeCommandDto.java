package com.example.pawgetherbe.controller.command.dto;

import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

public final class LikeCommandDto {

    public record LikeRequest(
            @NotBlank(message = "like 적용 대상을 입력해주세요.")
            String targetType,
            @NotNull
            Long targetId
    ) {}

    public record LikeResponse(
            String targetType,
            Long targetId,
            boolean isLiked
    ) {}
}
