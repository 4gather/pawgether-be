package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/petfairs")
public class PetFairQueryApi {

    private final ReadPostByIdUseCase readPostByIdUseCase;

    @GetMapping("/{petfairId}")
    public DetailPetFairResponse readDetailPetFair(@PathVariable("petfairId")Long petFairId) {
        return readPostByIdUseCase.readDetailPetFair(petFairId);
    }
}
