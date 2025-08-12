package com.example.pawgetherbe.usecase.jwt;

import java.util.Map;

public interface RefreshUseCase {
    Map<String, String> refresh(String authHeader, String refreshToken);
}
