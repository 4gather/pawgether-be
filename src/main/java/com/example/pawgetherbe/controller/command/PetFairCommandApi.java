package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateResponse;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateRequest;
import com.example.pawgetherbe.usecase.post.RegistryPostUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/petfair")
@RequiredArgsConstructor
@Slf4j
public class PetFairCommandApi {

    private final RegistryPostUseCase registryPostUseCase;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PetFairCreateResponse PetFairPostCreate(@Valid @ModelAttribute PetFairCreateRequest petFairCreateRequest) {
        return registryPostUseCase.postCreate(petFairCreateRequest);
    }
}
