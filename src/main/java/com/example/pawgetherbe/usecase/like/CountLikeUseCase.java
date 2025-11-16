package com.example.pawgetherbe.usecase.like;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.CountLikeResponse;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.TargetRequest;

import java.util.Set;

public interface CountLikeUseCase {
    CountLikeResponse countLikeDetail(TargetRequest targetRequest);
    Set<CountLikeResponse> countLikeList(LikeQueryDto.TargetRequests targetRequests);
}
