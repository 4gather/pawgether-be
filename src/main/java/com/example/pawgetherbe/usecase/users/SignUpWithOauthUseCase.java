package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.common.filter.dto.OauthDto.oauth2SignUpRequest;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequestResponse;

public interface SignUpWithOauthUseCase {
    userSignUpRequestResponse oauthSignUp(oauth2SignUpRequest oauthDto);
}
