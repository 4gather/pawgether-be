package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCountByStatusResponse;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;

public interface CountPostsUseCase {
    PetFairCountByStatusResponse countActiveByFilterStatus(PetFairFilterStatus status);
}
