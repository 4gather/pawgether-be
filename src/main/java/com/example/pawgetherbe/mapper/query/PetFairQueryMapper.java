package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.DetailPetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PetFairQueryMapper {

    DetailPetFairResponse toDetailPetFair(PetFairEntity petFairEntity);
}