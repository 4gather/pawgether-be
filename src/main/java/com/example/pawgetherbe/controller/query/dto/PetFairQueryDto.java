package com.example.pawgetherbe.controller.query.dto;

import com.example.pawgetherbe.controller.query.dto.PetFairImageQueryDto.PetFairImageUrlResponse;
import com.example.pawgetherbe.domain.status.PostStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class PetFairQueryDto {

    public record DetailPetFairResponse(
            Long petFairId,
            Long userId,
            String title,
            String postImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            String simpleAddress,
            String detailAddress,
            String petFairUrl,
            String content,
            Long counter,
            String latitude,
            String longitude,
            String mapUrl,
            String telNumber,
            PostStatus status,
            Instant createdAt,
            Instant updatedAt,
            List<PetFairImageUrlResponse> images
    ) {}
}
