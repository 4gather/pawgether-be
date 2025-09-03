package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.ConditionRequest;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairCountByStatusResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.SummaryPetFairWithCursorResponse;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.usecase.post.CountPostsUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import jakarta.validation.Valid;
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
    private final CountPostsUseCase countPostsUseCase;
    private final ReadPostsUseCase readPostsUseCase;

    @GetMapping("/{petfairId}")
    public DetailPetFairResponse readDetailPetFair(@PathVariable("petfairId")Long petFairId) {
        return readPostByIdUseCase.readDetailPetFair(petFairId);
    }

    @GetMapping("/count/{filterStatus}")
    public PetFairCountByStatusResponse countPetFairByStatus(@PathVariable("filterStatus") PetFairFilterStatus filterStatus) {
        return countPostsUseCase.countActiveByStatus(filterStatus);
    }

    @GetMapping
    public SummaryPetFairWithCursorResponse findAllPetFairs() {
        return readPostsUseCase.findAllPetFairs();
    }

    @GetMapping("/condition")
    public SummaryPetFairWithCursorResponse findPetFairsByCondition(@Valid ConditionRequest condition) {
        return readPostsUseCase.findPetFairsByCondition(condition);
    }
}
