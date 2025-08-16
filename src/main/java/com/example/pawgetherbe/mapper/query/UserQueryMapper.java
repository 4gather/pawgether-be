package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto;
import org.mapstruct.Mapper;

@Mapper
public interface UserQueryMapper {
    UserCommandDto.UserAccessTokenDto toAccessTokenDto(Long userId, String userRole);
}
