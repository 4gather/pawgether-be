package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.SummaryPetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PetFairQueryMapper {

    @Mapping(target="petFairId", source="id")
    @Mapping(target="userId", source="user.id")
    @Mapping(target="images", source="pairImages")
    DetailPetFairResponse toDetailPetFair(PetFairEntity petFairEntity);

    @Mapping(target="petFairId", source="id")
    SummaryPetFairResponse toSummaryPetFair(PetFairEntity entity);

    default String ImageUrl(PetFairImageEntity entity) {
        return (entity == null) ? null : entity.getImageUrl();
    }
}