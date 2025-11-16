package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.SummaryLikesByUserResponse;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import org.mapstruct.Mapper;

@Mapper
public interface LikeQueryMapper {

    SummaryLikesByUserResponse toLikeResponse(LikeEntity likeEntity);
}
