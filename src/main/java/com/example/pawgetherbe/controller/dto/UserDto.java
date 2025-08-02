package com.example.pawgetherbe.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public final class UserDto {

    public record userSignUpRequest(
            @NotNull(message = "닉네임을 입력해 주세요")
            @Size(min = 3, max = 20, message = "닉네임은 3~50자로 입력해주세요")
            String nickname,
            @NotNull(message = "이메일을 입력해 주세요")
            @Email(message = "이메일 형식을 지켜주세요")
            String email,
            @NotNull(message = "비밀번호를 입력해주세요")
            @Size(min = 8, message = "비밀번호는 8자 이상 입력해주세요")
            String password
            ) {}

    public record oauth2SignUpResponse(
            String accessToken,
            String refreshToken,
            String provider,
            String email,
            String nickname,
            String userImg
    ) {}

    public record OAuth2ResponseBody(
            String accessToken,
            String provider,
            String email,
            String nickname,
            String userImg
    ) {}
}
