package com.example.pawgetherbe.usecase.like;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.ExistsLikeResponse;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.TargetRequest;

import java.util.Set;

public interface ExistsLikeUseCase {
    ExistsLikeResponse isExistLikeDetail(TargetRequest targetRequest);
    Set<ExistsLikeResponse> isExistLikeList(LikeQueryDto.TargetRequests targetRequests);
}
