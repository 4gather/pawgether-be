package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.ReadBookmarkListResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.TargetResponse;
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
    public SummaryBookmarksResponse readBookmarkList(Long cursor) {

        List<ReadBookmarkListResponse> readBookmarkList;

        try {
            readBookmarkList = bookmarkQueryDSLRepository.readBookmarkList(cursor);
        } catch (Exception e) {
            throw new CustomException(FAIL_READ_BOOKMARK_LIST);
        }

        if(readBookmarkList == null || readBookmarkList.isEmpty()) {
            throw new CustomException(NOT_FOUND_BOOKMARK);
        }

        boolean hasMore = (readBookmarkList.size() == 11); // hasMore 고려(최대 반환 개수 + 1)

        if (hasMore) {
            // 반환할 10개의 게시글만 제공
            readBookmarkList.removeLast();
        }

        List<TargetResponse> bookmarkDtos = readBookmarkList.stream()
                .map(request -> bookmarkQueryMapper.toTargetResponse(request.petFairId(), true))
                .toList();

        long lastCursor = readBookmarkList.getLast().bookmarkId();

        return new SummaryBookmarksResponse(hasMore, lastCursor, bookmarkDtos);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<TargetResponse> isBookmarked(Set<Long> targetIds) {
        // PetFair에서 10개이하로만 아이디를 제공하므로 11개 이상인 경우는 비정상적인 접근이라 판단
        if (targetIds.size() > 10) {
            throw new CustomException(OVER_SIZE_TARGET_IDS);
        }

        // targetIds 중에서 REMOVED || 존재하지 않는 게시글일 때 에러 반환
        if (!targetRegistry.existsByTargetList("post", targetIds)) {
            throw new CustomException(NOT_FOUND_SOME_TARGET);
        }

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
