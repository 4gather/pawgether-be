package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.Oauth2SignUpResponse;

public interface SignUpWithOauthUseCase {
    Oauth2SignUpResponse oauthSignUp(String provider, String code);
}
