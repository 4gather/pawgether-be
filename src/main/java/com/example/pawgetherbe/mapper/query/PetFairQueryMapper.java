package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.SummaryPetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface PetFairQueryMapper {
    @Mapping(target = "petFairId", source = "id")
    DetailPetFairResponse toDetailPetFair(PetFairEntity petFairEntity);

    @Mapping(target = "petFairId", source = "id")
    SummaryPetFairResponse toSummaryPetFair(PetFairEntity entity);
}