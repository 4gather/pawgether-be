package com.example.pawgetherbe.usecase.users.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto;

public interface SignUpCommandUseCase {
    void signUp(UserCommandDto.UserSignUpRequest request);
}
