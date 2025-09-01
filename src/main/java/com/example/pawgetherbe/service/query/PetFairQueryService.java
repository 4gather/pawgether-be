package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCalendarResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCountByStatusResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper;
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository;
import com.example.pawgetherbe.usecase.post.CountPostsUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POST;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class PetFairQueryService implements ReadPostsUseCase, ReadPostByIdUseCase, CountPostsUseCase {

    private final PetFairQueryDSLRepository petFairQueryDSLRepository;

    private final PetFairQueryMapper petFairQueryMapper;

    @Override
    public PetFairCarouselResponse petFairCarousel() {
        var petFairCarousel = petFairQueryDSLRepository.petFairCarousel();
        if (petFairCarousel == null || petFairCarousel.isEmpty()) {
            throw new CustomException(NOT_FOUND_PET_FAIR_POSTER);
        }
        return new PetFairCarouselResponse(petFairCarousel);
    }

    @Override
    public PetFairCalendarResponse petFairCalendar(String date) {
        var petFairCalendar = petFairQueryDSLRepository.petFairCalendar(date);
        if (petFairCalendar == null || petFairCalendar.isEmpty()) {
            throw new CustomException(NOT_FOUND_PET_FAIR_CALENDAR);
        }
        return new PetFairCalendarResponse(petFairCalendar);
    }

    @Override
    @Transactional(readOnly = true)
    public DetailPetFairResponse readDetailPetFair(Long petFairId) {

        // Post Status 가 ACTIVE 인 것만 조회
        PetFairEntity readDetailPetFairEntity = petFairQueryDSLRepository.findActiveById(petFairId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR_POST));

        return petFairQueryMapper.toDetailPetFair(readDetailPetFairEntity);
    }

    @Override
    public PetFairCountByStatusResponse countActiveByStatus(PetFairFilterStatus status) {

        Long countActiveByFilteredStatus = petFairQueryDSLRepository.countActiveByStatus(status);

        return new PetFairCountByStatusResponse(status, countActiveByFilteredStatus);
    }
}
