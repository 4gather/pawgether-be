package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.OAuth2ResponseBody;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.Oauth2SignUpResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserWithRefreshTokenResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest;
import com.example.pawgetherbe.domain.entity.UserEntity;
import org.mapstruct.Mapper;

@Mapper
public interface UserCommandMapper {
    UserEntity toUserEntity(UserSignUpRequest userDto);
    SignInUserWithRefreshTokenResponse toSignInWithRefreshToken(UserEntity userEntity, String accessToken, String refreshToken);
     OAuth2ResponseBody toOAuth2ResponseBody(Oauth2SignUpResponse oauth2SignUpResponse);
    Oauth2SignUpResponse toOauth2SignUpResponse(UserEntity userEntity,String provider, String accessToken, String refreshToken);
    SignInUserResponse toSignInUserResponse(SignInUserWithRefreshTokenResponse response);
    UpdateUserResponse toUpdateUserResponse(UpdateUserRequest request);
    UserCommandDto.UserAccessTokenDto toAccessTokenDto(Long userId, String userRole);
}
