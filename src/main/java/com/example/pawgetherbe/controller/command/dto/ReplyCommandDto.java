package com.example.pawgetherbe.controller.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ReplyCommandDto {

    public record ReplyCreateRequest(
            @NotNull(message = "commentId을 입력해주세요.")
            long commentId,

            @NotBlank(message = "답글 내용을 입력해주세요.")
            String content
    ) {}
    public record ReplyCreateResponse(
            long replyId,
            long commentId,
            long userId,
            String content,
            String createdAt,
            String updatedAt
    ) {}

    public record ReplyUpdateRequest(
            @NotNull(message = "replyId을 입력해주세요.")
            long replyId,

            @NotNull(message = "commentId을 입력해주세요.")
            long commentId,

            @NotBlank(message = "답글 내용을 입력해주세요.")
            String content
    ) {}

    public record ReplyUpdateResponse(
            long replyId,
            long commentId,
            long userId,
            String content,
            String createdAt,
            String updatedAt
    ) {}
}
