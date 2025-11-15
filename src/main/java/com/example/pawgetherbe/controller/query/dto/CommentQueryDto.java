package com.example.pawgetherbe.controller.query.dto;

import java.util.List;

public final class CommentQueryDto {

    public record ReadCommentResponse(
            boolean hasMore,
            long nextCursor,
            List<ReadCommentDto> comments
    ) {}

    public record ReadCommentDto(
            long commentId,
            long petFairId,
            String nickName,
            String content,
            String createdAt,
            String updatedAt
    ) {}

    public record MainCommentResponse(
            List<MainCommentDto> comments
    ) {}

    public record MainCommentDto(
            long commentId,
            long petFairId,
            String nickName,
            String content,
            String createdAt,
            String updatedAt
    ) {}

    public record CommentCountDto(
            String targetType,
            long targetId,
            long count
    ) {}

    public record CommentCountResponse(
            CommentCountDto CommentCount
    ) {}
}
