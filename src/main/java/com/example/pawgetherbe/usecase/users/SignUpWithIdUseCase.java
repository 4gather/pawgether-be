package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequestResponse;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;

public interface SignUpWithIdUseCase {
    userSignUpRequestResponse signUp(userSignUpRequest request);
}
