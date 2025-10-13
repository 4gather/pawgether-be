package com.example.pawgetherbe.controller.query.dto;

public final class LikeQueryDto {

    public record SummaryLikesByUserResponse(
            Long id,
            Long targetId,
            String targetType
    ) {}
}
