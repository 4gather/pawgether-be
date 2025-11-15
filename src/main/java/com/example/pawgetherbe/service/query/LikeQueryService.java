package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.*;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import com.example.pawgetherbe.exception.query.LikeQueryErrorCode;
import com.example.pawgetherbe.mapper.query.LikeQueryMapper;
import com.example.pawgetherbe.repository.query.LikeQueryDSLRepository;
import com.example.pawgetherbe.service.checker.LikeTargetRegistry;
import com.example.pawgetherbe.usecase.like.CountLikeUseCase;
import com.example.pawgetherbe.usecase.like.ExistsLikeUseCase;
import com.example.pawgetherbe.usecase.like.ReadLikesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.pawgetherbe.exception.command.LikeCommandErrorCode.NOT_FOUND_LIKE;
import static com.example.pawgetherbe.exception.query.LikeQueryErrorCode.NOT_FOUND_SOME_TARGET;
import static com.example.pawgetherbe.exception.query.LikeQueryErrorCode.NOT_FOUND_TARGET;

@Service
@RequiredArgsConstructor
public class LikeQueryService implements ReadLikesUseCase, ExistsLikeUseCase, CountLikeUseCase {

    private final LikeTargetRegistry likeTargetRegistry;

    private final LikeQueryMapper likeQueryMapper;

    private final LikeQueryDSLRepository likeQueryDSLRepository;

    @Transactional(readOnly = true)
    @Override
    public List<SummaryLikesByUserResponse> readLikesByUser() {
        List<LikeEntity> likeEntities = likeQueryDSLRepository.findLikesByUser();

        if (likeEntities == null || likeEntities.isEmpty()) {
            throw new CustomException(NOT_FOUND_LIKE);
        }

        return likeEntities.stream()
                .map(likeQueryMapper::toLikeResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public CountLikeResponse countLikeDetail(TargetRequest targetRequest) {
        if (!isExistTarget(targetRequest)) {
            throw new CustomException(NOT_FOUND_TARGET);
        }

        long count = likeQueryDSLRepository.countLikeByTarget(targetRequest);

        return new CountLikeResponse(targetRequest.targetType(), targetRequest.targetId(), count);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<CountLikeResponse> countLikeList(TargetRequests targetRequests) {
        if (!isExistsTargetList(targetRequests)) {
            throw new CustomException(LikeQueryErrorCode.NOT_FOUND_SOME_TARGET);
        }

        return likeQueryDSLRepository.countLikeByTargetList(targetRequests);
    }

    @Transactional(readOnly = true)
    @Override
    public Set<ExistsLikeResponse> isExistLikeList(TargetRequests targetRequests) {
        if (!isExistsTargetList(targetRequests)) {
            throw new CustomException(NOT_FOUND_SOME_TARGET);
        }

        Set<Long> isExistsLikeIdList = likeQueryDSLRepository.existsLikedByUserAndTargetList(targetRequests);

        return isExistsLikeIdList.stream()
                .map(id -> new ExistsLikeResponse(targetRequests.targetType(), id, isExistsLikeIdList.contains(id)))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    @Override
    public ExistsLikeResponse isExistLikeDetail(TargetRequest targetRequest) {
        if (!isExistTarget(targetRequest)) {
            throw new CustomException(NOT_FOUND_TARGET);
        }

        Long isExistsLike = likeQueryDSLRepository.existsLikedByUserAndTarget(targetRequest);

        if (isExistsLike == null) {
            throw new CustomException(NOT_FOUND_LIKE);
        }

        return new ExistsLikeResponse(targetRequest.targetType(), targetRequest.targetId(), true);
    }

    private boolean isExistTarget(TargetRequest targetRequest) {
        return likeTargetRegistry.existsByOneTarget(targetRequest.targetType(), targetRequest.targetId());
    }

    private boolean isExistsTargetList(TargetRequests targetRequest) {
        return likeTargetRegistry.existsByTargetList(targetRequest.targetType(), targetRequest.targetIds());
    }
}
