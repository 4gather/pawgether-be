package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T23:23:16+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class LikeQueryMapperImpl implements LikeQueryMapper {

    @Override
    public LikeQueryDto.SummaryLikesByUserResponse toLikeResponse(LikeEntity likeEntity) {
        if ( likeEntity == null ) {
            return null;
        }

        Long id = null;
        Long targetId = null;
        String targetType = null;

        id = likeEntity.getId();
        targetId = likeEntity.getTargetId();
        targetType = likeEntity.getTargetType();

        LikeQueryDto.SummaryLikesByUserResponse summaryLikesByUserResponse = new LikeQueryDto.SummaryLikesByUserResponse( id, targetId, targetType );

        return summaryLikesByUserResponse;
    }
}
