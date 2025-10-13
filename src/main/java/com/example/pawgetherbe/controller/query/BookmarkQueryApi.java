package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarkByIdUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkQueryApi {

    private final ReadBookmarksUseCase readBookmarksUseCase;
    private final ReadBookmarkByIdUseCase readBookmarkByIdUseCase;

    @GetMapping
    public SummaryBookmarksResponse readBookmarks() {
        return readBookmarksUseCase.readBookmarks();
    }

    @GetMapping("/{petFairId}")
    public DetailPetFairResponse readDetailBookmarkPetFair(@PathVariable Long petFairId) {
        return readBookmarkByIdUseCase.readDetailBookmarkPetFair(petFairId);
    }
}
