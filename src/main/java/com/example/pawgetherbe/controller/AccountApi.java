package com.example.pawgetherbe.controller;

import com.example.pawgetherbe.config.OauthConfig;
import com.example.pawgetherbe.controller.dto.UserDto.OAuth2ResponseBody;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.usecase.users.DeleteUserUseCase;
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase;
import com.example.pawgetherbe.usecase.users.SignUpWithOauthUseCase;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountApi {

    private final SignUpWithIdUseCase signUpWithIdUseCase;
    private final SignUpWithOauthUseCase signUpWithOauthUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final OauthConfig oauthConfig;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Validated @RequestBody userSignUpRequest signUpRequest){
        signUpWithIdUseCase.signUp(signUpRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public void logout() {

    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void deleteAccount(String email, @RequestHeader("RefreshToken") String refreshTokenHeader) {
        String refreshToken = extractToken(refreshTokenHeader);
        deleteUserUseCase.deleteAccount(email, refreshToken);
    }

    @PostMapping("/signup/email")
    @ResponseStatus(HttpStatus.OK)
    public void signupEmailCheck(@RequestBody String email){
        signUpWithIdUseCase.signupEmailCheck(email);
    }

    @PostMapping("/signup/nickname")
    @ResponseStatus(HttpStatus.OK)
    public void signupNicknameCheck(@RequestBody String nickname){
        signUpWithIdUseCase.signupNicknameCheck(nickname);
    }

    @GetMapping("/oauth/{provider}")
    public void redirectToProvider(@PathVariable String provider, HttpServletResponse response) throws IOException {
        var providerProps = oauthConfig.getProviders().get(provider);
        if (providerProps == null) {
            throw new IllegalArgumentException("Unknown OAuth provider: " + provider);
        }

        String redirectUrl = UriComponentsBuilder
                .fromHttpUrl(providerProps.getAuthorizationUri())
                .queryParam("client_id", providerProps.getClientId())
                .queryParam("redirect_uri", providerProps.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("scope", String.join(" ", providerProps.getScope()))
                .build()
                .toUriString();

        response.sendRedirect(redirectUrl);
    }

    @GetMapping("/oauth/callback/{provider}")
    public ResponseEntity<OAuth2ResponseBody> oauthCallback(
            @PathVariable String provider,
            @RequestParam String code) {
        var oauth2SignUpResponse = signUpWithOauthUseCase.oauthSignUp(provider,code);

        ResponseCookie cookie = ResponseCookie.from("refresh_token", oauth2SignUpResponse.refreshToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(Duration.ofDays(7))
//                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new OAuth2ResponseBody(
                        oauth2SignUpResponse.accessToken(),
                        oauth2SignUpResponse.provider(),
                        oauth2SignUpResponse.email(),
                        oauth2SignUpResponse.nickname(),
                        oauth2SignUpResponse.userImg()
                ));
    }

    private String extractToken(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7); // "Bearer " 제거
        }
        return null;
    }
}
