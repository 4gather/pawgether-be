package com.example.pawgetherbe.common.filter.dto;

public final class OauthDto {

    public record oauth2SignUpRequest(
            String email,
            String nickname,
            String registrationId,
            String oauthUserId) {}

    public record oauth2SignUpResponse() {}
}
