package com.example.pawgetherbe.mapper;

import com.example.pawgetherbe.controller.dto.UserDto.SignInUserWithRefreshTokenResponse;
import com.example.pawgetherbe.controller.dto.UserDto.UserAccessTokenDto;
import com.example.pawgetherbe.controller.dto.UserDto.UserSignUpRequest;
import com.example.pawgetherbe.domain.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {
    UserEntity toUserEntity(UserSignUpRequest userDto);
    SignInUserWithRefreshTokenResponse toSignInWithRefreshToken(UserEntity userEntity, String accessToken, String refreshToken);
    UserAccessTokenDto toAccessTokenDto(Long userId, String userRole);
}
