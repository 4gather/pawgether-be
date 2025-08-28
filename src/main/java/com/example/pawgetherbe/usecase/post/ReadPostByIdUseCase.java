package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;

public interface ReadPostByIdUseCase {
    DetailPetFairResponse readDetailPetFair(Long petFairId);
}
