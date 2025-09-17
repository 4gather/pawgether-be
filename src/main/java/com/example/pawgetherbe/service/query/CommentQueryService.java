package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.mapper.query.CommentQueryMapper;
import com.example.pawgetherbe.repository.query.CommentQueryDSLRepository;
import com.example.pawgetherbe.repository.query.PetFairQueryRepository;
import com.example.pawgetherbe.repository.query.UserQueryRepository;
import com.example.pawgetherbe.usecase.comment.ReadCommentsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.example.pawgetherbe.domain.UserContext.getUserId;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;
import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_PET_FAIR_CALENDAR;

@Service
@RequiredArgsConstructor
public class CommentQueryService implements ReadCommentsUseCase {

    private final UserQueryRepository userQueryRepository;
    private final PetFairQueryRepository petFairQueryRepository;
    private final CommentQueryDSLRepository commentQueryDSLRepository;
    private final CommentQueryMapper commentQueryMapper;

    @Override
    public ReadCommentResponse readComments(long petfairId, long cursor) {
        var id = Long.valueOf(getUserId());
//        var id = 1L;

        if (!userQueryRepository.existsById(id)) {
            throw new CustomException(NOT_FOUND_USER);
        }
        if (!petFairQueryRepository.existsById(petfairId)) {
            throw new CustomException(NOT_FOUND_PET_FAIR_CALENDAR);
        }

        return commentQueryDSLRepository.readComments(petfairId, cursor);
    }
}
