package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.PetFairCreateResponse;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairRequest;
import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto.UpdatePetFairResponse;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface PetFairCommandMapper {
    PetFairEntity toPetFairEntity(PetFairCreateRequest petFairCreateRequest);
    PetFairEntity toPetFairEntity(UpdatePetFairRequest updatePetFairRequest);

    @Mapping(target = "petFairId", source = "id")
    @Mapping(target = "images", source = "pairImages")
    PetFairCreateResponse toPetFairCreateResponse(PetFairEntity petFairEntity);

    @Mapping(target = "petFairId", source = "id")
    @Mapping(target = "images", source = "pairImages")
    UpdatePetFairResponse toUpdatePetFairResponse(PetFairEntity petFairEntity);

    default List<String> mapImages(List<PetFairImageEntity> imageEntities) {
        if (imageEntities == null) return List.of();
        return imageEntities.stream()
                .map(PetFairImageEntity::getImageUrl)
                .toList();
    }
}
