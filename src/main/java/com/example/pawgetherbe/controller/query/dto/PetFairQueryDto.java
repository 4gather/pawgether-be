package com.example.pawgetherbe.controller.query.dto;

import java.time.LocalDate;
import java.util.List;

public final class PetFairQueryDto {

    public record PetFairCarouselResponse(
            List<PetFairPosterDto> petFairImages
    ) {}

    public record PetFairCalendarResponse(
            List<PetFairCalendarDto> petFairs
    ) {}

    public record PetFairPosterDto(
            Long petFairId,
            String posterImageUrl
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
}
