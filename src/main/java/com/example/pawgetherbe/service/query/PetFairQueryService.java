package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.*;
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

import java.util.List;

import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.*;

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
    @Transactional(readOnly = true)
    public SummaryPetFairWithCursorResponse findAllPetFairs(Cursor cursor) {
        List<PetFairEntity> activeListOrderByDesc = petFairQueryDSLRepository.findActiveListOrderByDesc(cursor);

        if (activeListOrderByDesc.isEmpty()) {
            throw new CustomException(NOT_FOUND_PET_FAIR_POST);
        }

        boolean hasMore = (activeListOrderByDesc.size() == 11); // hasMore 고려(최대 반환 개수 + 1)

        if (hasMore) {
            // 반환할 10개의 게시글만 제공
            activeListOrderByDesc.removeLast();
        }

        List<SummaryPetFairResponse> summaryPetFairResponseList = activeListOrderByDesc.stream()
                .map(petFairQueryMapper::toSummaryPetFair)
                .toList();
        Long nextCursor = activeListOrderByDesc.getLast().getId();

        return new SummaryPetFairWithCursorResponse(summaryPetFairResponseList, hasMore, nextCursor);
    }

    @Override
    public PetFairCountByStatusResponse countActiveByStatus(PetFairFilterStatus status) {

        Long countActiveByFilteredStatus = petFairQueryDSLRepository.countActiveByStatus(status);

        return new PetFairCountByStatusResponse(status, countActiveByFilteredStatus);
    }

    @Override
    public SummaryPetFairWithCursorResponse findPetFairsByCondition(ConditionRequest condition) {
        List<PetFairEntity> activeListByCondition = petFairQueryDSLRepository.findActiveByCondition(condition);

        if (activeListByCondition.isEmpty()) {
            throw new CustomException(NOT_FOUND_PET_FAIR_POST);
        }

        boolean hasMore = (activeListByCondition.size() == 11); // hasMore 고려(최대 반환 개수 + 1)

        if (hasMore) {
            // 반환할 10개의 게시글만 제공
            activeListByCondition.removeLast();
        }

        List<SummaryPetFairResponse> summaryPetFairResponseList = activeListByCondition.stream()
                .map(petFairQueryMapper::toSummaryPetFair)
                .toList();
        Long nextCursor = activeListByCondition.getLast().getId();

        return new SummaryPetFairWithCursorResponse(summaryPetFairResponseList, hasMore, nextCursor);
    }
}
