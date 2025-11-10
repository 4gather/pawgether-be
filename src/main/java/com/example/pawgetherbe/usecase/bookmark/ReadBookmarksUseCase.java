package com.example.pawgetherbe.usecase.bookmark;

import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.SummaryBookmarksResponse;

public interface ReadBookmarksUseCase {
    SummaryBookmarksResponse readBookmarks();
}
