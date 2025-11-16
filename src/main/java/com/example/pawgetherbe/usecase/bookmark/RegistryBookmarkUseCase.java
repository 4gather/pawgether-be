package com.example.pawgetherbe.usecase.bookmark;

import com.example.pawgetherbe.controller.command.dto.BookmarkCommandDto.BookmarkResponse;

public interface RegistryBookmarkUseCase {
    BookmarkResponse registryBookmark(Long petFairId);
}
