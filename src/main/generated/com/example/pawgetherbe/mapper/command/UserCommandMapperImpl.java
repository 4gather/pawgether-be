package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.UserRole;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T23:23:16+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class UserCommandMapperImpl implements UserCommandMapper {

    @Override
    public UserEntity toUserEntity(UserCommandDto.UserSignUpRequest userDto) {
        if ( userDto == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.email( userDto.email() );
        userEntity.password( userDto.password() );
        userEntity.nickName( userDto.nickName() );

        return userEntity.build();
    }

    @Override
    public UserCommandDto.SignInUserWithRefreshTokenResponse toSignInWithRefreshToken(UserEntity userEntity, String accessToken, String refreshToken) {
        if ( userEntity == null && accessToken == null && refreshToken == null ) {
            return null;
        }

        String email = null;
        String nickName = null;
        String userImg = null;
        if ( userEntity != null ) {
            email = userEntity.getEmail();
            nickName = userEntity.getNickName();
            userImg = userEntity.getUserImg();
        }
        String accessToken1 = null;
        accessToken1 = accessToken;
        String refreshToken1 = null;
        refreshToken1 = refreshToken;

        String provider = null;

        UserCommandDto.SignInUserWithRefreshTokenResponse signInUserWithRefreshTokenResponse = new UserCommandDto.SignInUserWithRefreshTokenResponse( accessToken1, refreshToken1, provider, email, nickName, userImg );

        return signInUserWithRefreshTokenResponse;
    }

    @Override
    public UserCommandDto.OAuth2ResponseBody toOAuth2ResponseBody(UserCommandDto.Oauth2SignUpResponse oauth2SignUpResponse) {
        if ( oauth2SignUpResponse == null ) {
            return null;
        }

        String accessToken = null;
        String provider = null;
        String email = null;
        String userImg = null;

        accessToken = oauth2SignUpResponse.accessToken();
        provider = oauth2SignUpResponse.provider();
        email = oauth2SignUpResponse.email();
        userImg = oauth2SignUpResponse.userImg();

        String nickname = null;

        UserCommandDto.OAuth2ResponseBody oAuth2ResponseBody = new UserCommandDto.OAuth2ResponseBody( accessToken, provider, email, nickname, userImg );

        return oAuth2ResponseBody;
    }

    @Override
    public UserCommandDto.Oauth2SignUpResponse toOauth2SignUpResponse(UserEntity userEntity, String provider, String accessToken, String refreshToken) {
        if ( userEntity == null && provider == null && accessToken == null && refreshToken == null ) {
            return null;
        }

        String email = null;
        String nickName = null;
        String userImg = null;
        if ( userEntity != null ) {
            email = userEntity.getEmail();
            nickName = userEntity.getNickName();
            userImg = userEntity.getUserImg();
        }
        String provider1 = null;
        provider1 = provider;
        String accessToken1 = null;
        accessToken1 = accessToken;
        String refreshToken1 = null;
        refreshToken1 = refreshToken;

        UserCommandDto.Oauth2SignUpResponse oauth2SignUpResponse = new UserCommandDto.Oauth2SignUpResponse( accessToken1, refreshToken1, provider1, email, nickName, userImg );

        return oauth2SignUpResponse;
    }

    @Override
    public UserCommandDto.SignInUserResponse toSignInUserResponse(UserCommandDto.SignInUserWithRefreshTokenResponse response) {
        if ( response == null ) {
            return null;
        }

        String accessToken = null;
        String provider = null;
        String email = null;
        String nickName = null;
        String userImg = null;

        accessToken = response.accessToken();
        provider = response.provider();
        email = response.email();
        nickName = response.nickName();
        userImg = response.userImg();

        UserCommandDto.SignInUserResponse signInUserResponse = new UserCommandDto.SignInUserResponse( accessToken, provider, email, nickName, userImg );

        return signInUserResponse;
    }

    @Override
    public UserCommandDto.UpdateUserResponse toUpdateUserResponse(UserCommandDto.UpdateUserRequest request) {
        if ( request == null ) {
            return null;
        }

        String nickName = null;
        String userImg = null;

        nickName = request.nickName();
        userImg = request.userImg();

        UserCommandDto.UpdateUserResponse updateUserResponse = new UserCommandDto.UpdateUserResponse( nickName, userImg );

        return updateUserResponse;
    }

    @Override
    public UserCommandDto.UserAccessTokenDto toAccessTokenDto(Long userId, String userRole) {
        if ( userId == null && userRole == null ) {
            return null;
        }

        Long id = null;
        UserRole role = null;

        UserCommandDto.UserAccessTokenDto userAccessTokenDto = new UserCommandDto.UserAccessTokenDto( id, role );

        return userAccessTokenDto;
    }
}
