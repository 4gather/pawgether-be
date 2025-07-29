package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.oauth2SignUpResponse;

public interface SignUpWithOauthUseCase {
    oauth2SignUpResponse oauthSignUp(String provider, String code);
}
