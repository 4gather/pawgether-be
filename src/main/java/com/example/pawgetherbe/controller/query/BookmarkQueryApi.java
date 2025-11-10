package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkDto.TargetResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.usecase.bookmark.IsBookmarkedUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarkByIdUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkQueryApi {

    private final ReadBookmarksUseCase readBookmarksUseCase;
    private final ReadBookmarkByIdUseCase readBookmarkByIdUseCase;
    private final IsBookmarkedUseCase isBookmarkedUseCase;

    @GetMapping
    public SummaryBookmarksResponse readBookmarks() {
        return readBookmarksUseCase.readBookmarks();
    }

    @GetMapping("/{petFairId}")
    public DetailPetFairResponse readDetailBookmarkPetFair(@PathVariable Long petFairId) {
        return readBookmarkByIdUseCase.readDetailBookmarkPetFair(petFairId);
    }

    @GetMapping("/exists")
    public Set<TargetResponse> isBookmarked(@RequestBody Set<Long> targetIds) {
        return isBookmarkedUseCase.isBookmarked(targetIds);
    }
}
