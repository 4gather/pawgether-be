package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.status.PostStatus;
import com.example.pawgetherbe.mapper.query.PetFairQueryMapper;
import com.example.pawgetherbe.repository.query.PetFairQueryRepository;
import com.example.pawgetherbe.usecase.post.ReadPostByIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.NOT_FOUND_POST;

@Service
@RequiredArgsConstructor
public class PetFairQueryService implements ReadPostByIdUseCase {

    private final PetFairQueryRepository petFairQueryRepository;
    private final PetFairQueryMapper petFairQueryMapper;

    @Transactional(readOnly = true)
    public DetailPetFairResponse readDetailPetFair(Long petFairId) {

        // Post Status 가 ACTIVE 인 것만 조회
        PetFairEntity readDetailPetFairEntity = petFairQueryRepository.findByIdAndPostStatus(
                petFairId,
                PostStatus.ACTIVE
        ).orElseThrow(() -> new CustomException(NOT_FOUND_POST));

        return petFairQueryMapper.toDetailPetFair(readDetailPetFairEntity);
    }
}
