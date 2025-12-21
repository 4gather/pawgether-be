package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto.TargetResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookmarkQueryMapper {

    @Mapping(target = "targetId", source = "petFairId")
    TargetResponse toTargetResponse(long petFairId, boolean isBookmarked);
}
