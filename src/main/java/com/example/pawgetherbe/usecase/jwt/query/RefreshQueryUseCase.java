package com.example.pawgetherbe.usecase.jwt.query;

import java.util.Map;

public interface RefreshQueryUseCase {
    Map<String, String> refresh(String authHeader, String refreshToken);
}
