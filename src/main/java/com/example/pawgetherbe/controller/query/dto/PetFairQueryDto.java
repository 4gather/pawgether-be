package com.example.pawgetherbe.controller.query.dto;

import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

public final class PetFairQueryDto {

    public record ConditionRequest(
            @NotBlank(message = "검색어를 입력해주세요.")
            String keyword,
            Cursor cursor
    ) {}

    public record Cursor(
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate startDate,
            Long petFairId
    ) {}

    public record PetFairCarouselResponse(
            List<PetFairPosterDto> petFairImages
    ) {}

    public record PetFairCalendarResponse(
            List<PetFairCalendarDto> petFairs
    ) {}

    public record PetFairCalendarDto(
        Long petFairId,
        Long counter,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalDate endDate,
        String simpleAddress
    ) {}

    public record PetFairPosterDto(
            Long petFairId,
            String posterImageUrl
    ) {}

    public record DetailPetFairResponse(
            Long petFairId,
            Long userId,
            String title,
            String posterImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            String simpleAddress,
            String detailAddress,
            String petFairUrl,
            String content,
            Long counter,
//            String latitude,
//            String longitude,
//            String mapUrl,
            String telNumber,
            PetFairStatus status,
            Instant createdAt,
            Instant updatedAt,
            List<String> images
    ) {}

    public record PetFairCountByStatusResponse(
            PetFairFilterStatus status,
            Long count
    ) {}

    public record SummaryPetFairResponse(
            Long petFairId,
            Long counter,
            String title,
            String posterImageUrl,
            LocalDate startDate,
            LocalDate endDate,
            String simpleAddress
    ){}

    public record SummaryPetFairWithCursorResponse(
            List<SummaryPetFairResponse> petFairSummaries,
            Boolean hasMore,
            Long nextCursor
    ) {}
}
