package com.example.pawgetherbe.controller.query.dto;

import java.util.List;

public final class BookmarkQueryDto {
    public record SummaryBookmarksResponse(
            boolean hasMore,
            long nextCursor,
            List<TargetResponse> bookmarkResponseList
    ) {}

    public record ReadBookmarkListResponse(
            long bookmarkId,
            long petFairId
    ) {}

    public record TargetResponse(
            long targetId,
            boolean isBookmarked
    ) {}
}
