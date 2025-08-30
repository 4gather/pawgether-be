package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCarouselResponse;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main")
public class PetFairMainQueryApi {

    private final ReadPostsUseCase readPostsUseCase;

    @GetMapping("/carousel")
    public PetFairCarouselResponse petFairCarousel() {
        return readPostsUseCase.petFairCarousel();
    }
}
