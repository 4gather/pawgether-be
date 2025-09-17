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
            String updatedAt,
            int heart
    ) {}
}
