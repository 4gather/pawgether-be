package com.example.pawgetherbe.mapper;

import com.example.pawgetherbe.controller.dto.UserDto.UserSignUpRequest;
import com.example.pawgetherbe.domain.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(UserSignUpRequest userDto);
}
