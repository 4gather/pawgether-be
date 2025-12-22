package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.BookmarkCommandDto.BookmarkResponse;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.mapper.command.BookmarkCommandMapper;
import com.example.pawgetherbe.repository.command.BookmarkCommandRepository;
import com.example.pawgetherbe.repository.command.PetFairCommandRepository;
import com.example.pawgetherbe.repository.command.UserCommandRepository;
import com.example.pawgetherbe.usecase.bookmark.CancelBookmarkUseCase;
import com.example.pawgetherbe.usecase.bookmark.RegistryBookmarkUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.pawgetherbe.exception.command.BookmarkCommandErrorCode.*;
import static com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.NOT_FOUND_PET_FAIR;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class BookmarkCommandService implements RegistryBookmarkUseCase, CancelBookmarkUseCase {
    private final BookmarkCommandMapper bookmarkCommandMapper;

    private final UserCommandRepository userCommandRepository;
    private final PetFairCommandRepository petFairCommandRepository;
    private final BookmarkCommandRepository bookmarkCommandRepository;

    @Transactional
    @Override
    public BookmarkResponse cancelBookmark(Long petFairId) {
        BookmarkEntity bookmarkEntity = bookmarkCommandRepository.findByPetFair_Id(petFairId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_BOOKMARK));
//        UserEntity userEntity = userCommandRepository.findById(Long.parseLong(UserContext.getUserId()))
//                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
        UserEntity userEntity = userCommandRepository.findById(1L)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        PetFairEntity petFairEntity = petFairCommandRepository.findById(petFairId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR));

        try {
            bookmarkEntity.removeUser(userEntity);
            bookmarkEntity.removePetFair(petFairEntity);
            bookmarkCommandRepository.deleteByPetFair_Id(petFairId);

            return new BookmarkResponse(petFairId, false);
        } catch (Exception e) {
            throw new CustomException(FAIL_CANCEL_BOOKMARK);
        }
    }

    @Transactional
    @Override
    public BookmarkResponse registryBookmark(Long petFairId) {

//        UserEntity userEntity = userCommandRepository.findById(Long.parseLong(UserContext.getUserId()))
//                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
        UserEntity userEntity = userCommandRepository.findById(1L)
                .orElseThrow(() -> new CustomException(NOT_FOUND_USER));
        PetFairEntity petFairEntity = petFairCommandRepository.findById(petFairId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR));

        // 해당 게시글이 이미 북마크인지 확인하는 로직 필요
        List<BookmarkEntity> bookmarkEntityList = petFairEntity.getBookmarkEntities();
        boolean isAlreadyExists = bookmarkEntityList.stream()
                .anyMatch(entity -> entity.getPetFair().getId().equals(petFairId));

        if (isAlreadyExists) {
            throw new CustomException(ALREADY_BOOKMARK);
        }

        BookmarkEntity bookmarkEntity = bookmarkCommandMapper.toEntity(userEntity, petFairEntity);

        try {
            bookmarkEntity.addUser(userEntity);
            bookmarkEntity.addPetFair(petFairEntity);
            bookmarkCommandRepository.save(bookmarkEntity);

            return new BookmarkResponse(petFairId, true);
        } catch (Exception e) {
            throw new CustomException(FAIL_CREATE_BOOKMARK);
        }
    }
}
