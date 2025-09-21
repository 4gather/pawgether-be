package com.example.pawgetherbe.controller.query.dto;

import java.util.List;

public final class ReplyQueryDto {

    public record ReplyReadResponse(
            boolean hasMore,
            long nextCursor,
            List<ReplyReadDto> replies
    ) {}

    public record ReplyReadDto(
            long replyId,
            long commentId,
            String nickName,
            String content,
            String createdAt,
            String updatedAt,
            int heart
    ) {}
}
