package com.example.pawgetherbe.usecase.users;

public interface DeleteUserUseCase {
    void deleteAccount(String email, String refreshToken);
}
