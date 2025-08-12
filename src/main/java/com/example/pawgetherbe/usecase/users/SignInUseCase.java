package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.SignInUserRequest;
import com.example.pawgetherbe.controller.dto.UserDto.SignInUserWithRefreshTokenResponse;

public interface SignInUseCase {

    SignInUserWithRefreshTokenResponse signIn(SignInUserRequest signInRequest);
}
