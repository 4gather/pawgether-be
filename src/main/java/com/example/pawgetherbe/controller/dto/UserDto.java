package com.example.pawgetherbe.controller.dto;

import com.example.pawgetherbe.domain.status.UserRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public final class UserDto {

    public record UserAccessTokenDto(
            Long id,
            UserRole role
    ) {}

    public record EmailCheckRequest(
            String email
    ) {}

    public record NickNameCheckRequest(
            String nickName
    ) {}

    public record UserSignUpRequest(
            @NotNull(message = "닉네임을 입력해 주세요")
            @Pattern(regexp = "^[a-zA-Z0-9가-힣_]{3,20}$", message = "닉네임은 영문, 숫자, 한글, 언더바(_)만 사용할 수 있습니다.")
            String nickName,
            @NotNull(message = "이메일을 입력해 주세요")
            @Email(message = "이메일 형식을 지켜주세요")
            String email,
            @NotNull(message = "비밀번호를 입력해주세요")
            @Pattern(
                    regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\\d!@#$%^&*()_+=-]{8,20}$",
                    message = "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해주세요"
            )
            String password
            ) {}
    public record UpdateUserRequest(
            @Pattern(regexp = "^[a-zA-Z0-9가-힣_]{3,20}$", message = "닉네임은 영문, 숫자, 한글, 언더바(_)만 사용할 수 있습니다.")
            String nickName,
            String userImg
    ) {}

    public record SignInUserRequest(
        @NotBlank(message = "이메일을 입력해 주세요")
        @Email(message = "이메일 형식을 지켜주세요")
        String email,
        @NotBlank(message = "비밀번호를 입력해주세요")
        @Pattern(
                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()_+=-])[A-Za-z\\d!@#$%^&*()_+=-]{8,20}$",
                message = "비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해주세요"
        )
        String password
    ) {}

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record UpdateUserResponse(
            String nickName,
            String userImg
    ) {}

    public record Oauth2SignUpResponse(
            String accessToken,
            String refreshToken,
            String provider,
            String email,
            String nickName,
            String userImg
    ) {}

    public record OAuth2ResponseBody(
            String accessToken,
            String provider,
            String email,
            String nickname,
            String userImg
    ) {}

    public record SignInUserResponse(
        String accessToken,
        String provider,
        String email,
        String nickName,
        String userImg
    ) {}

    public record SignInUserWithRefreshTokenResponse(
            String accessToken,
            String refreshToken,
            String provider,
            String email,
            String nickName,
            String userImg
    ) {}
}
