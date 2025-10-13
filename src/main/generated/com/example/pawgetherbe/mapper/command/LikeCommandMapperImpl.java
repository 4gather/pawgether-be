package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-12T23:33:27+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class LikeCommandMapperImpl implements LikeCommandMapper {

    @Override
    public LikeEntity toEntity(LikeCommandDto.LikeRequest likeRequest) {
        if ( likeRequest == null ) {
            return null;
        }

        LikeEntity.LikeEntityBuilder likeEntity = LikeEntity.builder();

        likeEntity.targetId( likeRequest.targetId() );
        likeEntity.targetType( likeRequest.targetType() );

        return likeEntity.build();
    }
}
