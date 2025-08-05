package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto;

public interface SignUpWithIdUseCase {
    void signUp(UserDto.UserSignUpRequest request);
    void signupEmailCheck(String email);
    void signupNicknameCheck(String nickname);
}
