package com.example.pawgetherbe.controller.query.dto;

import java.util.List;

public final class PetFairQueryDto {

    public record PetFairCarouselResponse(
            List<PetFairPosterDto> petFairImages
    ) {}

    public record PetFairPosterDto(Long petFairId, String posterImageUrl) {}
}
