package com.example.pawgetherbe.usecase.bookmark;

import com.example.pawgetherbe.controller.query.dto.BookmarkDto.SummaryBookmarksResponse;

public interface ReadBookmarksUseCase {
    SummaryBookmarksResponse readBookmarks();
}
