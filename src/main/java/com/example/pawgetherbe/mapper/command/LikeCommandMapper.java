package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import org.mapstruct.Mapper;

@Mapper
public interface LikeCommandMapper {

    LikeEntity toEntity(LikeRequest likeRequest);
}
