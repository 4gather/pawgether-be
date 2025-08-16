package com.example.pawgetherbe.usecase.users.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserWithRefreshTokenResponse;

public interface SignInCommandUseCase {

    SignInUserWithRefreshTokenResponse signIn(SignInUserRequest signInRequest);
}
