package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCalendarResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.SummaryPetFairWithCursorResponse;

public interface ReadPostsUseCase {
    PetFairCarouselResponse petFairCarousel();
    PetFairCalendarResponse petFairCalendar(String date);
    SummaryPetFairWithCursorResponse findAllPetFairs();
}
