package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.*;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.usecase.post.CountPostsUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import com.example.pawgetherbe.usecase.post.ReadPostsUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/count")
    public PetFairCountByStatusResponse countPetFairByFilterStatus(@RequestParam("filterStatus") PetFairFilterStatus filterStatus) {
        return countPostsUseCase.countActiveByFilterStatus(filterStatus);
    }

    @GetMapping("/filter")
    public SummaryPetFairWithCursorResponse findPetFairsByFilterStatus(@RequestParam("filterStatus") PetFairFilterStatus filterStatus,
                                                                      @ModelAttribute Cursor cursor) {
        return readPostsUseCase.findPetFairsByFilterStatus(filterStatus, cursor);
    }

    @GetMapping
    public SummaryPetFairWithCursorResponse findAllPetFairs(@ModelAttribute Cursor cursor) {
        return readPostsUseCase.findAllPetFairs(cursor);
    }

    @GetMapping("/condition")
    public SummaryPetFairWithCursorResponse findPetFairsByCondition(@Valid @ModelAttribute ConditionRequest condition) {
        return readPostsUseCase.findPetFairsByCondition(condition);
    }
}
