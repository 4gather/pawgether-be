package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.BookmarkDto.DetailBookmarkedPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.BookmarkDto.SummaryBookmarksResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.mapper.query.BookmarkQueryMapper;
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper;
import com.example.pawgetherbe.repository.query.BookmarkQueryDSLRepository;
import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarkByIdUseCase;
import com.example.pawgetherbe.usecase.bookmark.ReadBookmarksUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.pawgetherbe.exception.query.BookmarkQueryErrorCode.NOT_FOUND_BOOKMARK;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_POSTER;

@Service
@RequiredArgsConstructor
public class BookmarkQueryService implements ReadBookmarksUseCase, ReadBookmarkByIdUseCase {

    private final BookmarkQueryMapper bookmarkQueryMapper;
    private final PetFairQueryMapper petFairQueryMapper;

    private final PetFairQueryDSLRepository petFairQueryDSLRepository;
    private final BookmarkQueryDSLRepository bookmarkQueryDSLRepository;

    @Transactional(readOnly = true)
    public SummaryBookmarksResponse readBookmarks() {
        List<BookmarkEntity> bookmarkEntities = bookmarkQueryDSLRepository.readBookmarks();

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
}
