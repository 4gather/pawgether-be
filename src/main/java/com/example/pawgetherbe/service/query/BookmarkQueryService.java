package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.DetailBookmarkedPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.TargetResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.mapper.query.BookmarkQueryMapper;
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper;
import com.example.pawgetherbe.repository.query.BookmarkQueryDSLRepository;
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository;
import com.example.pawgetherbe.usecase.bookmark.IsBookmarkedUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarkByIdUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.pawgetherbe.exception.query.BookmarkQueryErrorCode.*;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER;

@Service
@RequiredArgsConstructor
public class BookmarkQueryService implements ReadBookmarksUseCase, ReadBookmarkByIdUseCase, IsBookmarkedUseCase {

    private final BookmarkQueryMapper bookmarkQueryMapper;
    private final PetFairQueryMapper petFairQueryMapper;

    private final PetFairQueryDSLRepository petFairQueryDSLRepository;
    private final BookmarkQueryDSLRepository bookmarkQueryDSLRepository;

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
    public DetailPetFairResponse readDetailBookmarkPetFair(Long petFairId) {
        PetFairEntity petFairEntity = petFairQueryDSLRepository.findActiveById(petFairId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR_POSTER));

        return petFairQueryMapper.toDetailPetFair(petFairEntity);
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
