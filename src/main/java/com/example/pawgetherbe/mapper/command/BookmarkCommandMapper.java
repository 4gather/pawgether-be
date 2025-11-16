package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface BookmarkCommandMapper {
    @Mapping(target="id", ignore = true)
    BookmarkEntity toEntity(UserEntity user, PetFairEntity petFair);
}
