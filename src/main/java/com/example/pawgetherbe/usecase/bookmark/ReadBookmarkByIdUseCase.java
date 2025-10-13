package com.example.pawgetherbe.usecase.bookmark;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;

public interface ReadBookmarkByIdUseCase {
    DetailPetFairResponse readDetailBookmarkPetFair(Long petFairId);
}
