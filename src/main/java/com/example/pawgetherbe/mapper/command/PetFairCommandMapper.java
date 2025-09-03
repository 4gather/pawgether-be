package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PetFairCommandMapper {
    PetFairEntity toPetFairEntity(PetFairCreateRequest petFairCreateRequest);

    @Mapping(target = "images", source = "pairImages")
    PetFairCreateResponse toPetFairCreateResponse(PetFairEntity petFairEntity);

    default List<String> mapImages(List<PetFairImageEntity> imageEntities) {
        if (imageEntities == null) return List.of();
        return imageEntities.stream()
                .map(PetFairImageEntity::getImageUrl)
                .toList();
    }
}
