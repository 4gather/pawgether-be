package com.example.pawgetherbe.usecase.users.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto.Oauth2SignUpResponse;

public interface SignUpCommandOauthUseCase {
    Oauth2SignUpResponse oauthSignUp(String provider, String code);
}
