package com.example.pawgetherbe.controller;

import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase;
import com.example.pawgetherbe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Slf4j
public class AccountApi {

    private final SignUpWithIdUseCase signUpWithIdUseCase;
    private final JwtUtil jwtUtil;

    @PostMapping("/account")
    public ResponseEntity<String> signup(@Validated @RequestBody userSignUpRequest signUpRequest){
        var user = signUpWithIdUseCase.signUp(signUpRequest);
        var accessToken = jwtUtil.generateAccessToken(user);

        log.info("Access token: {}", accessToken);
        log.info("refreshToken token: {}", user.refreshToken());

        ResponseCookie cookie = ResponseCookie.from("refresh_token", user.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
//                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(accessToken);
    }
}
