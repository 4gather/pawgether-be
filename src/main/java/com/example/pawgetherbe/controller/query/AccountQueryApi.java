package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.UserQueryDto.NickNameCheckRequest;
import com.example.pawgetherbe.controller.query.dto.UserQueryDto.EmailCheckRequest;
import com.example.pawgetherbe.usecase.jwt.query.RefreshQueryUseCase;
import com.example.pawgetherbe.usecase.users.query.SignUpQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.example.pawgetherbe.common.filter.JwtAuthFilter.REQUEST_HEADER_AUTH;
import static com.example.pawgetherbe.exception.query.UserQueryErrorCode.INVALID_FORMAT_EMAIL;
import static com.example.pawgetherbe.exception.query.UserQueryErrorCode.INVALID_FORMAT_NICKNAME;
import static com.example.pawgetherbe.util.ValidationUtil.isValidEmail;
import static com.example.pawgetherbe.util.ValidationUtil.isValidNickName;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountQueryApi {

    private final RefreshQueryUseCase refreshQueryUseCase;
    private final SignUpQueryUseCase signUpQueryUseCase;
    private static final String SAME_SITE_STRICT = "Strict";
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7일

    @PostMapping("/signup/email")
    @ResponseStatus(HttpStatus.OK)
    public void signupEmailCheck(@RequestBody EmailCheckRequest emailCheckRequest) {
        var email = emailCheckRequest.email();
        if (!isValidEmail(email)) {
            throw new CustomException(INVALID_FORMAT_EMAIL);
        }
        signUpQueryUseCase.signupEmailCheck(email);
    }

    @PostMapping("/signup/nickname")
    @ResponseStatus(HttpStatus.OK)
    public void signupNickNameCheck(@RequestBody NickNameCheckRequest nickNameCheckRequest){
        var nickName = nickNameCheckRequest.nickName();
        if (!isValidNickName(nickName)) {
            throw new CustomException(INVALID_FORMAT_NICKNAME);
        }
        signUpQueryUseCase.signupNicknameCheck(nickName);
    }

    @PostMapping("/refresh")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<String> refresh(@RequestHeader(REQUEST_HEADER_AUTH) String authHeader,
                                          @CookieValue(name = "refreshToken") String refreshToken) {

        Map<String, String> authTokens = refreshQueryUseCase.refresh(authHeader, refreshToken);

        ResponseCookie refreshTokenCookieHeader = buildRefreshTokenCookieHeader(authTokens.get("refreshToken"));

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieHeader.toString())
                .body(authTokens.get("accessToken"));
    }

    private ResponseCookie buildRefreshTokenCookieHeader(String refreshToken) {
        return ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(REFRESH_TOKEN_VALIDITY_SECONDS)
                .sameSite(SAME_SITE_STRICT)
                .build();
    }
}
