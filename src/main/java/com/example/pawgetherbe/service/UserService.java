package com.example.pawgetherbe.service;

import com.example.pawgetherbe.common.oauth.OAuthProviderSpec;
import com.example.pawgetherbe.config.OauthConfig;
import com.example.pawgetherbe.config.OauthConfig.Provider;
import com.example.pawgetherbe.controller.dto.UserDto.oauth2SignUpResponse;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.domain.entity.OauthEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.UserRole;
import com.example.pawgetherbe.domain.status.UserStatus;
import com.example.pawgetherbe.mapper.UserMapper;
import com.example.pawgetherbe.repository.OauthRepository;
import com.example.pawgetherbe.repository.UserRepository;
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase;
import com.example.pawgetherbe.usecase.users.SignUpWithOauthUseCase;
import com.example.pawgetherbe.util.JwtUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.pawgetherbe.util.EncryptUtil.generateRefreshToken;
import static com.example.pawgetherbe.util.EncryptUtil.passwordEncode;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements SignUpWithIdUseCase, SignUpWithOauthUseCase {

    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    private final JwtUtil jwtUtil;
    private final OauthConfig oauthConfig;

    @Override
    @Transactional
    public void signUp(userSignUpRequest request) {
        var userEntity = userMapper.toUserEntity(request);
        var userEntityBuilder = userEntity
                    .toBuilder()
                    .password(passwordEncode(request.password()))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();

        userRepository.save(userEntityBuilder);
    }

    @Override
    public void signupEmailCheck(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, email + " 이미 가입된 계정입니다."
            );
        }
    }

    @Override
    @Transactional
    public oauth2SignUpResponse oauthSignUp(String provider, String code) {
        log.info("oauthSignUp start");
        Map<String, Object> userInfo = fetchOAuthUserInfo(provider, code);

        String email = (String) userInfo.get("email");
        String nickname = (String) userInfo.get("nickname");
        String providerId = (String) userInfo.get("providerId");
        log.info("email = {}", email);
        log.info("nickname = {}", nickname);
        log.info("providerId = {}", providerId);

        var oauthCheck = oauthRepository.existsByOauthProviderIdAndOauthProvider(providerId, provider);
        var userCheck = userRepository.existsByEmail(email);

        UserEntity user;

        if(userCheck && !oauthCheck) {
            user = userRepository.findByEmail(email);
            var oauthEntity = OauthEntity.builder()
                    .oauthProviderId(providerId)
                    .oauthProvider(provider)
                    .user(user)
                    .build();
            oauthRepository.save(oauthEntity);
            var token = getToken(user);

            return new oauth2SignUpResponse(
                    token.get("accessToken"),
                    token.get("refreshToken"),
                    null,
                    email,
                    nickname,
                    user.getUserImg()
            );
        } else if (oauthCheck){
            user = oauthRepository.findByOauthProviderId(providerId).get().getUser();
        }else {
            UserEntity newUser = UserEntity.builder()
                    .email(email)
                    .nickName(nickname)
                    .password(passwordEncode(UUID.randomUUID().toString().substring(0, 8))) // 랜덤 값
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();

            user = userRepository.save(newUser);

            var oauthEntity = OauthEntity.builder()
                    .oauthProviderId(providerId)
                    .oauthProvider(provider)
                    .user(user)
                    .build();
            oauthRepository.save(oauthEntity);
        }

        var token = getToken(user);

        return new oauth2SignUpResponse(
                token.get("accessToken"),
                token.get("refreshToken"),
                provider,
                email,
                nickname,
                user.getUserImg()
        );
    }

    public Map<String, String> getToken(UserEntity userEntity) {
        String refreshToken = generateRefreshToken();
        String accessToken = jwtUtil.generateAccessToken(userEntity);

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
            throw new IllegalArgumentException("지원하지 않는 oauth2: " + provider);
        }
        log.info("getClientId = {}", oauth.getClientId());
        log.info("clientSecret = {}", oauth.getClientSecret());

        OAuth20Service service = null;

        if ("kakao".equals(provider)) {
            return fetchKakaoUserInfo(code, oauth);
        } else {
            service = new ServiceBuilder(oauth.getClientId())
                    .apiSecret(oauth.getClientSecret())
                    .callback(oauth.getRedirectUri())
                    .defaultScope(String.join(" ", oauth.getScope()))
                    .build(new OAuthProviderSpec(
                            oauth.getAuthorizationUri(),
                            oauth.getTokenUri()
                    ));
        }

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
                        "nickname", root.path("name").asText(),
                        "providerId", root.path("sub").asText()
                );
                case "naver" -> {
                    JsonNode naverResponse = root.path("response");
                    yield Map.of(
                            "email", naverResponse.path("email").asText(),
                            "nickname", naverResponse.path("name").asText(),
                            "providerId", naverResponse.path("id").asText()
                    );
                }
                default -> throw new IllegalArgumentException("Unsupported provider: " + provider);
            };

        }catch (Exception e) {
            throw new RuntimeException("OAuth 처리 중 오류 발생", e);
        }
    }
    private Map<String, Object> fetchKakaoUserInfo(String code, Provider oauth) {
        // 1. 토큰 요청
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", oauth.getClientId());
        params.add("redirect_uri", oauth.getRedirectUri());
        params.add("code", code);

        if (StringUtils.hasText(oauth.getClientSecret())) {
            params.add("client_secret", oauth.getClientSecret());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<JsonNode> tokenResponse = new RestTemplate()
                .postForEntity(oauth.getTokenUri(), request, JsonNode.class);

        String accessToken = tokenResponse.getBody().get("access_token").asText();

        // 2. 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        ResponseEntity<JsonNode> userInfoResponse = new RestTemplate()
                .exchange(oauth.getUserInfoUri(), HttpMethod.GET, userInfoRequest, JsonNode.class);

        JsonNode root = userInfoResponse.getBody();
        JsonNode kakaoAccount = root.path("kakao_account");
        JsonNode profile = kakaoAccount.path("profile");

        String email = kakaoAccount.path("email").asText(null);
        if (email == null || email.isEmpty()) {
            email = "random-" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        }

        return Map.of(
                "email", email,
                "nickname", profile.path("nickname").asText(),
                "providerId", root.path("id").asText()
        );
    }
}
