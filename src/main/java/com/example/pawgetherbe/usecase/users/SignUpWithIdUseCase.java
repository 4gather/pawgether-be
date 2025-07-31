package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;

public interface SignUpWithIdUseCase {
    void signUp(userSignUpRequest request);
    void signupEmailCheck(String email);
    void signupNicknameCheck(String nickname);
}
