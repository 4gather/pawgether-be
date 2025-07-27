package com.example.pawgetherbe.mapper;

import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequestResponse;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.domain.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserEntity toUserEntity(userSignUpRequest userDto);
    userSignUpRequestResponse toUserSignUpResponseDto(String accessToken, String refreshToken);
}
