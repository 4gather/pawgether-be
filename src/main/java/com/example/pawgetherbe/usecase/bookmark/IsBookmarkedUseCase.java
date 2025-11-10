package com.example.pawgetherbe.usecase.bookmark;

import com.example.pawgetherbe.controller.query.dto.BookmarkDto.TargetResponse;

import java.util.Set;

public interface IsBookmarkedUseCase {
    Set<TargetResponse> isBookmarked(Set<Long> targetIds);
}
