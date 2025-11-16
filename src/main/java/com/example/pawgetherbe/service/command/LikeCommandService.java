package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeResponse;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.TargetRequest;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.mapper.command.LikeCommandMapper;
import com.example.pawgetherbe.repository.command.LikeCommandRepository;
import com.example.pawgetherbe.repository.query.LikeQueryDSLRepository;
import com.example.pawgetherbe.repository.query.UserQueryRepository;
import com.example.pawgetherbe.service.checker.LikeTargetRegistry;
import com.example.pawgetherbe.usecase.like.CancelLikeUseCase;
import com.example.pawgetherbe.usecase.like.LikeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.exception.command.LikeCommandErrorCode.*;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;

@Service
@RequiredArgsConstructor
public class LikeCommandService implements LikeUseCase, CancelLikeUseCase {

    private final LikeCommandRepository likeCommandRepository;
    private final LikeQueryDSLRepository likeQueryDSLRepository;
    private final UserQueryRepository userQueryRepository;

    private final LikeCommandMapper likeCommandMapper;

    private final LikeTargetRegistry likeTargetRegistry;

    @Transactional
    @Override
    public LikeResponse addLike(LikeRequest likeRequest) {

//        Long userId = Long.parseLong(UserContext.getUserId());
        Long userId = 1L;

        UserEntity userEntity = userQueryRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        // 해당 Target 존재 유무
        if (!likeTargetRegistry.existsByOneTarget(likeRequest.targetType(), likeRequest.targetId())) {
            throw new CustomException(NOT_FOUND_TARGET);
        }

        // 존재하는 Target이 Like Table에 존재 유무
        boolean existsLikeByUserAndTarget = likeQueryDSLRepository.hasUserLikedTarget(
                new TargetRequest(likeRequest.targetType(), likeRequest.targetId()));

        if (existsLikeByUserAndTarget) {
            throw new CustomException(ALREADY_FOUND_LIKE);
        };

        LikeEntity likeEntity = likeCommandMapper.toEntity(likeRequest);
        likeEntity.addUser(userEntity);

        try {
            LikeEntity savedLikeEntity = likeCommandRepository.save(likeEntity);
            existsLikeByUserAndTarget = true;

            long likeCount = likeQueryDSLRepository.countLikeByTarget(
                    new TargetRequest(likeRequest.targetType(), likeRequest.targetId()));

            return new LikeResponse(likeRequest.targetType(), likeRequest.targetId(), existsLikeByUserAndTarget);
        } catch (Exception e) {
            throw new CustomException(FAIL_LIKE);
        }
    }

    @Transactional
    @Override
    public LikeResponse cancelLike(LikeRequest likeRequest) {

//        Long userId = Long.parseLong(UserContext.getUserId());
        Long userId = 1L;

        UserEntity userEntity = userQueryRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        LikeEntity likeEntity = likeQueryDSLRepository.findLikeByUserAndTarget(likeRequest)
                .orElseThrow(() -> new CustomException(NOT_FOUND_LIKE));

        likeEntity.removeUser(userEntity);

        try {
            likeCommandRepository.deleteById(likeEntity.getId());
            return new LikeResponse(likeRequest.targetType(),likeRequest.targetId(),false);
        } catch (Exception e) {
            throw new CustomException(FAIL_CANCEL_LIKE);
        }
    }
}
