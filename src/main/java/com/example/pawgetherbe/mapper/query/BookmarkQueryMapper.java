package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkDto.DetailBookmarkedPetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookmarkQueryMapper {
    @Mapping(target="petFairId", source="entity.id")
    DetailBookmarkedPetFairResponse toDetailBookmarkedPetPairResponse(PetFairEntity entity, boolean isBookmark);
}
