package com.example.pawgetherbe.usecase.jwt.command;

import java.util.Map;

public interface RefreshCommandUseCase {
    Map<String, String> refresh(String authHeader, String refreshToken);
}
