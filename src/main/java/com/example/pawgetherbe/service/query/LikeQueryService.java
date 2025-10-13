package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.SummaryLikesByUserResponse;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import com.example.pawgetherbe.mapper.query.LikeQueryMapper;
import com.example.pawgetherbe.repository.query.LikeQueryDSLRepository;
import com.example.pawgetherbe.usecase.like.ReadLikesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.pawgetherbe.exception.command.LikeCommandErrorCode.NOT_FOUND_LIKE;

@Service
@RequiredArgsConstructor
public class LikeQueryService implements ReadLikesUseCase {

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
}
