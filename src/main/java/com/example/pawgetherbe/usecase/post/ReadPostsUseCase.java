package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse;

public interface ReadPostsUseCase {
    PetFairCarouselResponse petFairCarousel();
}
