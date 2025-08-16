package com.example.pawgetherbe.service.command;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.common.oauth.OAuthProviderSpec;
import com.example.pawgetherbe.config.OauthConfig;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserWithRefreshTokenResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.Oauth2SignUpResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserResponse;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserAccessTokenDto;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest;
import com.example.pawgetherbe.domain.entity.OauthEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.UserRole;
import com.example.pawgetherbe.domain.status.UserStatus;
import com.example.pawgetherbe.mapper.command.UserCommandMapper;
import com.example.pawgetherbe.repository.command.OauthCommandRepository;
import com.example.pawgetherbe.repository.command.UserCommandRepository;
import com.example.pawgetherbe.usecase.users.command.DeleteUserCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.EditUserCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignInCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignOutCommandUseCase;
import com.example.pawgetherbe.usecase.users.command.SignUpCommandOauthUseCase;
import com.example.pawgetherbe.usecase.users.command.SignUpCommandUseCase;
import com.example.pawgetherbe.util.EncryptUtil;
import com.example.pawgetherbe.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.pawgetherbe.domain.UserContext.getUserId;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.CONFLICT_USER;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.OAUTH_PROVIDER_NOT_SUPPORTED;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.UNAUTHORIZED_PASSWORD;
import static com.example.pawgetherbe.util.EncryptUtil.generateRefreshToken;
import static com.example.pawgetherbe.util.EncryptUtil.passwordEncode;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandService implements SignUpCommandOauthUseCase, SignUpCommandUseCase, SignOutCommandUseCase, DeleteUserCommandUseCase, EditUserCommandUseCase, SignInCommandUseCase {

    private final OauthCommandRepository oauthCommandRepository;
    private final UserCommandRepository userCommandRepository;
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtUtil jwtUtil;
    private final OauthConfig oauthConfig;
    private final UserCommandMapper userCommandMapper;
    public static final int REFRESH_TOKEN_VALIDITY_SECONDS = 7 * 24 * 60 * 60; // 7일

    @Override
    public void deleteAccount(String refreshToken) {
        var id = Long.valueOf(getUserId());
        var oauthCheck = oauthCommandRepository.existsByUser_Id(id);
        if(oauthCheck) {
            oauthCommandRepository.deleteByUser_Id(id);
        }
        userCommandRepository.deleteById(id);
        redisTemplate.delete(refreshToken);
    }

    @Override
    public UpdateUserResponse updateUserInfo(UpdateUserRequest request) {
        var id = getUserId();
        var user = userCommandRepository.findById(Long.valueOf(id)).orElseThrow(() ->
                new CustomException(NOT_FOUND_USER)
        );

        user.updateProfile(request.nickName(), request.userImg());

        return userCommandMapper.toUpdateUserResponse(request);
    }

    @Override
    public SignInUserWithRefreshTokenResponse signIn(SignInUserRequest signInRequest) {
        UserEntity userEntity = userCommandRepository.findByEmail(signInRequest.email());

        // 회원 존재 유무 체크
        if (userEntity == null) {
            throw new CustomException(NOT_FOUND_USER);
        }

        // password 유효성 확인
        if (!isMatchedPassword(signInRequest.password(), userEntity.getPassword())) {
            throw new CustomException(UNAUTHORIZED_PASSWORD);
        }

        // access token 발급
        String accessToken = jwtUtil.generateAccessToken(new UserAccessTokenDto(userEntity.getId(), userEntity.getRole()));

        // refresh token 발급
        String refreshToken = generateRefreshToken();
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(userEntity.getId()), Duration.ofDays(REFRESH_TOKEN_VALIDITY_SECONDS));

        return userCommandMapper.toSignInWithRefreshToken(userEntity, accessToken, refreshToken);
    }

    @Override
    public void signOut(String refreshToken) {
        redisTemplate.delete(refreshToken);
    }

    @Override
    public Oauth2SignUpResponse oauthSignUp(String provider, String code) {
        log.info("oauthSignUp start");
        Map<String, Object> userInfo = fetchOAuthUserInfo(provider, code);

        String email = (String) userInfo.get("email");
        String nickName = (String) userInfo.get("nickname");
        String providerId = (String) userInfo.get("providerId");
        log.info("email = {}", email);
        log.info("nickName = {}", nickName);
        log.info("providerId = {}", providerId);

        var oauthCheck = oauthCommandRepository.existsByOauthProviderIdAndOauthProvider(providerId, provider);
        var userCheck = userCommandRepository.existsByEmail(email);

        UserEntity user;

        if(userCheck && !oauthCheck) {
            user = userCommandRepository.findByEmail(email);
            var oauthEntity = OauthEntity.builder()
                    .oauthProviderId(providerId)
                    .oauthProvider(provider)
                    .user(user)
                    .build();
            oauthCommandRepository.save(oauthEntity);
            var token = getToken(user);
            user.updateRole(UserRole.USER_BOTH);

            return userCommandMapper.toOauth2SignUpResponse(
                    user,
                    null,
                    token.get("accessToken"),
                    token.get("refreshToken"));
        } else if (oauthCheck){
            user = oauthCommandRepository.findByOauthProviderId(providerId).get().getUser();
        }else {
            if (userCommandRepository.existsByNickName(nickName)){
                nickName = UUID.randomUUID().toString().substring(0, 8);
            }
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .nickName(nickName)
                    .password(passwordEncode(UUID.randomUUID().toString().substring(0, 8))) // 랜덤 값
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER_AUTH)
                    .build();

            user = userCommandRepository.save(newUser);

            var oauthEntity = OauthEntity.builder()
                    .oauthProviderId(providerId)
                    .oauthProvider(provider)
                    .user(user)
                    .build();
            oauthCommandRepository.save(oauthEntity);
        }

        var token = getToken(user);

        return userCommandMapper.toOauth2SignUpResponse(
                user,
                provider,
                token.get("accessToken"),
                token.get("refreshToken")
        );
    }

    @Override
    public void signUp(UserSignUpRequest request) {
        if(userCommandRepository.existsByEmail(request.email())){
            throw new CustomException(CONFLICT_USER);
        }
        var userEntity = userCommandMapper.toUserEntity(request);
        var userEntityBuilder = userEntity
                .toBuilder()
                .password(passwordEncode(request.password()))
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER_EMAIL)
                .build();

        userCommandRepository.save(userEntityBuilder);
    }

    public Map<String, String> getToken(UserEntity userEntity) {
        String refreshToken = generateRefreshToken();
        String accessToken = jwtUtil.generateAccessToken(
                new UserAccessTokenDto(userEntity.getId(), userEntity.getRole())
        );

        // Redis에 저장 (key: refreshToken, value: userId)
        redisTemplate.opsForValue().set(refreshToken, String.valueOf(userEntity.getId()), Duration.ofDays(7));

        // 토큰을 Map으로 반환
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("refreshToken", refreshToken);
        tokenMap.put("accessToken", accessToken);
        return tokenMap;
    }

    public Map<String, Object> fetchOAuthUserInfo(String provider, String code){
        var oauth = oauthConfig.getProviders().get(provider);

        if (oauth == null) {
            throw new CustomException(OAUTH_PROVIDER_NOT_SUPPORTED);
        }
        log.info("getClientId = {}", oauth.getClientId());
        log.info("clientSecret = {}", oauth.getClientSecret());

//        OAuth20Service service = null;

        var service = new ServiceBuilder(oauth.getClientId())
                .apiSecret(oauth.getClientSecret())
                .callback(oauth.getRedirectUri())
                .defaultScope(String.join(" ", oauth.getScope()))
                .build(new OAuthProviderSpec(
                        oauth.getAuthorizationUri(),
                        oauth.getTokenUri()
                ));

        try {
            OAuth2AccessToken accessToken = service.getAccessToken(code);

            OAuthRequest request = new OAuthRequest(Verb.GET, oauth.getUserInfoUri());
            service.signRequest(accessToken, request);
            Response response = service.execute(request);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());

            return switch (provider) {
                case "google" -> Map.of(
                        "email", root.path("email").asText(),
                        "nickname", root.path("nickname").asText(),
                        "providerId", root.path("sub").asText()
                );
                case "naver" -> {
                    JsonNode naverResponse = root.path("response");
                    yield Map.of(
                            "email", naverResponse.path("email").asText(),
                            "nickname", naverResponse.path("nickname").asText(),
                            "providerId", naverResponse.path("id").asText()
                    );
                }
                default -> throw new CustomException(OAUTH_PROVIDER_NOT_SUPPORTED);
            };

        }catch (Exception e) {
            throw new RuntimeException("OAuth 처리 중 오류 발생", e);
        }
    }

    private boolean isMatchedPassword(String plainPassword, String encryptPassword) {
        return EncryptUtil.matches(plainPassword, encryptPassword);
    }
}
