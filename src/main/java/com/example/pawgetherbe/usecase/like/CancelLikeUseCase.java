package com.example.pawgetherbe.usecase.like;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeResponse;

public interface CancelLikeUseCase {
    LikeResponse cancelLike(LikeRequest request);
}
