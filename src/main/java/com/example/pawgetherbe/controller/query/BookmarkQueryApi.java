package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.TargetResponse;
import com.example.pawgetherbe.usecase.bookmark.IsBookmarkedUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkQueryApi {

    private final ReadBookmarksUseCase readBookmarksUseCase;
    private final IsBookmarkedUseCase isBookmarkedUseCase;

    @GetMapping
    public SummaryBookmarksResponse readBookmarks() {
        return readBookmarksUseCase.readBookmarks();
    }

    @GetMapping("/exists")
    public Set<TargetResponse> isBookmarked(@RequestParam Set<Long> targetIds) {
        return isBookmarkedUseCase.isBookmarked(targetIds);
    }
}
