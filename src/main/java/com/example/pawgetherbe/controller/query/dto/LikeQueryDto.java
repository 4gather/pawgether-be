package com.example.pawgetherbe.controller.query.dto;

import java.util.Set;

public final class LikeQueryDto {

    public record TargetRequest(
            String targetType,
            Long targetId
    ) {}

    public record TargetRequests(
            String targetType,
            Set<Long> targetIds
    ) {}

    public record SummaryLikesByUserResponse(
            Long id,
            Long targetId,
            String targetType
    ) {}

    public record ExistsLikeResponse(
            String targetType,
            Long targetId,
            boolean isLiked
    ) {}

    public record CountLikeResponse(
            String targetType,
            Long targetId,
            Long count
    ) {}
}
