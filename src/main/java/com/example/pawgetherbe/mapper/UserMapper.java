package com.example.pawgetherbe.mapper;

import com.example.pawgetherbe.controller.dto.UserDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.dto.UserDto.UpdateUserResponse;
import com.example.pawgetherbe.controller.dto.UserDto.SignInUserResponse;
import com.example.pawgetherbe.controller.dto.UserDto.OAuth2ResponseBody;
import com.example.pawgetherbe.controller.dto.UserDto.Oauth2SignUpResponse;
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
    OAuth2ResponseBody toOAuth2ResponseBody(Oauth2SignUpResponse oauth2SignUpResponse);
    Oauth2SignUpResponse toOauth2SignUpResponse(UserEntity userEntity,String provider, String accessToken, String refreshToken);
    SignInUserResponse toSignInUserResponse(SignInUserWithRefreshTokenResponse response);
    
    @org.mapstruct.Mapping(target = "userImg", source = "userImg")
    @org.mapstruct.Mapping(target = "nickName", source = "nickName")
    UpdateUserResponse toUpdateUserResponse(UpdateUserRequest request);
}
