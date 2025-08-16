package com.example.pawgetherbe.usecase.users.query;

public interface SignUpQueryUseCase {
    void signupEmailCheck(String email);
    void signupNicknameCheck(String nickname);
}
