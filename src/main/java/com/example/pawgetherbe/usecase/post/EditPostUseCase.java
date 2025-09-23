package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairResponse;

public interface EditPostUseCase {
    UpdatePetFairResponse updatePetFairPost(Long petFairId, UpdatePetFairRequest updatePetFairRequest);
}
