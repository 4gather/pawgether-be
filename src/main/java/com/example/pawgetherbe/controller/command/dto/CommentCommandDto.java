package com.example.pawgetherbe.controller.command.dto;

import jakarta.validation.constraints.NotBlank;

public final class CommentCommandDto {
    public record CommentCreateRequest(
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {}

    public record CommentCreateResponse(
            long commentId,
            long userId,
            long petFairId,
            String content,
            String createdAt,
            String updatedAt,
            int heart
    ) {}

    public record CommentUpdateRequest(
            @NotBlank(message = "내용을 입력해주세요.")
            String content
    ) {}

    public record CommentUpdateResponse(
            long commentId,
            long userId,
            long petFairId,
            String content,
            String createdAt,
            String updatedAt,
            int heart
    ) {}
}
