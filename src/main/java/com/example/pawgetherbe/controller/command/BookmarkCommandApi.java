package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.BookmarkCommandDto.BookmarkResponse;
import com.example.pawgetherbe.usecase.bookmark.CancelBookmarkUseCase;
import com.example.pawgetherbe.usecase.bookmark.RegistryBookmarkUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bookmark")
public class BookmarkCommandApi {

    private final RegistryBookmarkUseCase registryBookmarkUseCase;
    private final CancelBookmarkUseCase cancelBookmarkUseCase;

    @PostMapping
    public BookmarkResponse registryBookmark(@RequestParam Long petFairId) {
        return registryBookmarkUseCase.registryBookmark(petFairId);
    }

    @DeleteMapping
    public BookmarkResponse cancelBookmark(@RequestParam Long petFairId) {
        return cancelBookmarkUseCase.cancelBookmark(petFairId);
    }
}
