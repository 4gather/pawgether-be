package com.example.pawgetherbe.controller.command.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;

public final class LikeCommandDto {

    public record LikeRequest(
            @NotBlank(message = "like 적용 대상을 입력해주세요.")
            String targetType,
            @NotNull
            Long targetId
    ) {}

    public record LikeResponse(
            boolean isLiked,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            Instant createdAt,
            @JsonInclude(JsonInclude.Include.NON_EMPTY)
            Long likeCount
    ) {}
}
