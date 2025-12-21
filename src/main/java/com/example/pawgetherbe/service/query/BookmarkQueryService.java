package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.DetailBookmarkedPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.TargetResponse;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.mapper.query.BookmarkQueryMapper;
import com.example.pawgetherbe.repository.query.BookmarkQueryDSLRepository;
import com.example.pawgetherbe.service.checker.TargetRegistry;
import com.example.pawgetherbe.usecase.bookmark.IsBookmarkedUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.pawgetherbe.exception.query.BookmarkQueryErrorCode.*;

@Service
@RequiredArgsConstructor
public class BookmarkQueryService implements ReadBookmarksUseCase, IsBookmarkedUseCase {

    private final BookmarkQueryMapper bookmarkQueryMapper;

    private final BookmarkQueryDSLRepository bookmarkQueryDSLRepository;

    private final TargetRegistry targetRegistry;

    @Transactional(readOnly = true)
    public SummaryBookmarksResponse readBookmarks() {

        List<BookmarkEntity> bookmarkEntities;

        try {
            bookmarkEntities = bookmarkQueryDSLRepository.readBookmarks();
        } catch (Exception e) {
            throw new CustomException(FAIL_READ_BOOKMARK_LIST);
        }

        if (bookmarkEntities == null || bookmarkEntities.isEmpty()) {
            throw new CustomException(NOT_FOUND_BOOKMARK);
        }

        boolean hasMore = (bookmarkEntities.size() == 11); // hasMore 고려(최대 반환 개수 + 1)

        if (hasMore) {
            // 반환할 10개의 게시글만 제공
            bookmarkEntities.removeLast();
        }

        List<DetailBookmarkedPetFairResponse> bookmarkDtos = bookmarkEntities.stream()
                .map(request -> bookmarkQueryMapper.toDetailBookmarkedPetPairResponse(request.getPetFair(), true))
                .toList();

        Long nextCursor = bookmarkDtos.getLast().petFairId();

        return new SummaryBookmarksResponse(hasMore, nextCursor, bookmarkDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<TargetResponse> isBookmarked(Set<Long> targetIds) {
        try {
            Set<Long> isBookmarkedPetFair = bookmarkQueryDSLRepository.existsBookmark(targetIds);

            return isBookmarkedPetFair.stream()
                    .map(post -> new TargetResponse(post, targetIds.contains(post)))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            throw new CustomException(FAIL_READ_BOOKMARK_STATUS);
        }
    }
}
