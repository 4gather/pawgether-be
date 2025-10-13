package com.example.pawgetherbe.usecase.like;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.SummaryLikesByUserResponse;

import java.util.List;

public interface ReadLikesUseCase {
    List<SummaryLikesByUserResponse> readLikesByUser();
}
