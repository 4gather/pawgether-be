package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCalendarResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse;
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetFairQueryService implements ReadPostsUseCase {

    private final PetFairQueryDSLRepository petFairQueryDSLRepository;

    @Override
    public PetFairCarouselResponse petFairCarousel() {
        var petFairCarousel = petFairQueryDSLRepository.petFairCarousel();
        if (petFairCarousel == null) {
            throw new CustomException(NOT_FOUND_PET_FAIR_POSTER);
        }
        return new PetFairCarouselResponse(petFairCarousel);
    }

    @Override
    public PetFairCalendarResponse petFairCalendar(String date) {
        var petFairCalendar = petFairQueryDSLRepository.petFairCalendar(date);
        if (petFairCalendar == null) {
            throw new CustomException(NOT_FOUND_PET_FAIR_CALENDAR);
        }
        return new PetFairCalendarResponse(petFairCalendar);
    }
}
