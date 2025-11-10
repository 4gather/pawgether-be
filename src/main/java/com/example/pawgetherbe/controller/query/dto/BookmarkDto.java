package com.example.pawgetherbe.controller.query.dto;

import java.time.LocalDate;
import java.util.List;

public final class BookmarkDto {
    public record SummaryBookmarksResponse(
            boolean hasMore,
            Long nextCursor,
            List<DetailBookmarkedPetFairResponse> petFairResponses
    ) {}

    public record DetailBookmarkedPetFairResponse(
            Long petFairId,
            Long counter,
            String title,
            String posterImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            String simpleAddress,
            boolean isBookmark
    ) {}

    public record TargetResponse(
            long targetId,
            boolean isBookmarked
    ) {}
}
