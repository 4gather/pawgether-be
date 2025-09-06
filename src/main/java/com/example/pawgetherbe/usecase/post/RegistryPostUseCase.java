package com.example.pawgetherbe.usecase.post;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateResponse;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateRequest;

public interface RegistryPostUseCase {
    PetFairCreateResponse postCreate(PetFairCreateRequest petFairCommandDto);
}
