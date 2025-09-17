package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.*;

public interface ReadPostsUseCase {
    PetFairCarouselResponse petFairCarousel();
    PetFairCalendarResponse petFairCalendar(String date);
    SummaryPetFairWithCursorResponse findAllPetFairs(Cursor cursor);
    SummaryPetFairWithCursorResponse findPetFairsByCondition(ConditionRequest condition);
}
