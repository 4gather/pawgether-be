package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateRequest;
import com.example.pawgetherbe.domain.status.CommentStatus;
import com.example.pawgetherbe.mapper.command.CommentCommandMapper;
import com.example.pawgetherbe.repository.command.CommentCommandRepository;
import com.example.pawgetherbe.repository.command.PetFairCommandRepository;
import com.example.pawgetherbe.repository.command.UserCommandRepository;
import com.example.pawgetherbe.usecase.comment.RegistryCommentUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.domain.UserContext.getUserId;
import static com.example.pawgetherbe.exception.command.CommentCommandErrorCode.NOT_FOUND_COMMENT;
import static com.example.pawgetherbe.exception.command.PetFairCommandErrorCode.NOT_FOUND_PET_FAIR;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentCommandService implements RegistryCommentUseCase {

    private final CommentCommandRepository commentCommandRepository;
    private final PetFairCommandRepository petFairCommandRepository;
    private final UserCommandRepository userCommandRepository;

    private final CommentCommandMapper commentCommandMapper;

    @Override
    @Transactional
    public CommentCreateResponse createComment(long petfairId, CommentCreateRequest request) {
        var id = Long.valueOf(getUserId());
        var user = userCommandRepository.findById(id).orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        var petfair = petFairCommandRepository.findById(petfairId).orElseThrow(() -> new CustomException(NOT_FOUND_PET_FAIR));

        var commentEntity = commentCommandMapper.toCreateEntity(request);
        var commentEntityBuilder = commentEntity.toBuilder()
                                    .user(user)
                                    .petFair(petfair)
                                    .status(CommentStatus.ACTIVE)
                                    .build();

        var comment = commentCommandRepository.save(commentEntityBuilder);

        return commentCommandMapper.toCreateResponse(comment);
    }

    @Override
    @Transactional
    public CommentUpdateResponse updateComment(long petfairId, long commentId, CommentUpdateRequest request) {
        var id = Long.valueOf(getUserId());
        var user = userCommandRepository.existsById(id);
        if(!user) {
            throw new CustomException(NOT_FOUND_USER);
        }

        var petfair = petFairCommandRepository.existsById(petfairId);
        if(!petfair) {
            throw new CustomException(NOT_FOUND_PET_FAIR);
        }

        var comment = commentCommandRepository.findById(commentId).orElseThrow(() -> new CustomException(NOT_FOUND_COMMENT));

        comment.updateContent(request.content());

        return commentCommandMapper.toCommentUpdateResponse(comment);
    }
}
