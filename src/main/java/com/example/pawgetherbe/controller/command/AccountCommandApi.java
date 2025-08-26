package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.config.OauthConfig;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.PasswordEditRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.OAuth2ResponseBody;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserWithRefreshTokenResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest;
import com.example.pawgetherbe.mapper.command.UserCommandMapper;
import com.example.pawgetherbe.usecase.jwt.command.RefreshCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.DeleteUserCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.EditUserCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignInCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignOutCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignUpCommandOauthUseCase;
import com.example.pawgetherbe.usecase.users.command.SignUpCommandUseCase;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import java.util.Map;

import static com.example.pawgetherbe.common.filter.JwtAuthFilter.REQUEST_HEADER_AUTH;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED;
import static com.example.pawgetherbe.service.command.UserCommandService.REFRESH_TOKEN_VALIDITY_SECONDS;


@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountCommandApi {

    private final RefreshCommandUseCase refreshCommandUseCase;
    private final SignUpCommandUseCase signUpCommandUseCase;
    private final SignUpCommandOauthUseCase signUpCommandOauthUseCase;
    private final DeleteUserCommandUseCase deleteUserCommandUseCase;
    private final SignOutCommandUseCase signOutCommandUseCase;
    private final EditUserCommandUseCase editUserCommandUseCase;
    private final SignInCommandUseCase signInCommandUseCase;

    private final UserCommandMapper userCommandMapper;
    private final OauthConfig oauthConfig;

    private static final String SAME_SITE_STRICT = "Strict";

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    public void signup(@Validated @RequestBody UserSignUpRequest signUpRequest){
        signUpCommandUseCase.signUp(signUpRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CookieValue(value = "refresh_token") String refreshToken) {
        signOutCommandUseCase.signOut(refreshToken);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAccount(@CookieValue(value = "refresh_token") String refreshToken) {
        deleteUserCommandUseCase.deleteAccount(refreshToken);
    }

    @PatchMapping
    public UpdateUserResponse updateUserInfo(@RequestBody UpdateUserRequest request) {
        var updateUserResponse = editUserCommandUseCase.updateUserInfo(request);

        return updateUserResponse;
    }

    @GetMapping("/oauth/{provider}")
    public void redirectToProvider(@PathVariable String provider, HttpServletResponse response) throws IOException {
        var providerProps = oauthConfig.getProviders().get(provider);
        if (providerProps == null) {
            throw new CustomException(OAUTH_PROVIDER_NOT_SUPPORTED);
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
        var oauth2SignUpResponse = signUpCommandOauthUseCase.oauthSignUp(provider,code);

        ResponseCookie cookie = buildRefreshTokenCookieHeader(oauth2SignUpResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(userCommandMapper.toOAuth2ResponseBody(oauth2SignUpResponse));
    }

    @PostMapping
    public ResponseEntity<SignInUserResponse> signIn(@RequestBody @Valid SignInUserRequest signInRequest) {
        SignInUserWithRefreshTokenResponse user = signInCommandUseCase.signIn(signInRequest);

        ResponseCookie refreshTokenCookieHeader = buildRefreshTokenCookieHeader(user.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookieHeader.toString())
                .body(userCommandMapper.toSignInUserResponse(user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@RequestHeader(REQUEST_HEADER_AUTH) String authHeader,
                                          @CookieValue(name = "refreshToken") String refreshToken) {

        Map<String, String> authTokens = refreshCommandUseCase.refresh(authHeader, refreshToken);

        ResponseCookie refreshTokenCookieHeader = buildRefreshTokenCookieHeader(authTokens.get("refreshToken"));

        return ResponseEntity.status(HttpStatus.CREATED)
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

    @PatchMapping("/password")
    public void editPassword(@RequestBody @Valid PasswordEditRequest passwordEditRequest) {
        editUserCommandUseCase.updatePassword(passwordEditRequest);
    }
}
