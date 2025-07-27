package com.example.pawgetherbe.service;

import com.example.pawgetherbe.common.filter.dto.OauthDto.oauth2SignUpRequest;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequestResponse;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.UserRole;
import com.example.pawgetherbe.domain.status.UserStatus;
import com.example.pawgetherbe.mapper.Oauth2Mapper;
import com.example.pawgetherbe.mapper.UserMapper;
import com.example.pawgetherbe.repository.OauthRepository;
import com.example.pawgetherbe.repository.UserRepository;
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase;
import com.example.pawgetherbe.usecase.users.SignUpWithOauthUseCase;
import com.example.pawgetherbe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import static com.example.pawgetherbe.util.EncryptUtil.generateRefreshToken;
import static com.example.pawgetherbe.util.EncryptUtil.passwordEncode;

@Service
@RequiredArgsConstructor
public class UserService implements SignUpWithIdUseCase, SignUpWithOauthUseCase {

    private final UserRepository userRepository;
    private final OauthRepository oauthRepository;
    private final UserMapper userMapper;
    private final Oauth2Mapper oauth2Mapper;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;

    @Override
    @Transactional
    public userSignUpRequestResponse signUp(userSignUpRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException(
                    HttpStatus.BAD_REQUEST.value() + " " + request.email() + " 이미 가입된 계정 입니다."
            );
        }

        var userEntity = userMapper.toUserEntity(request);
        var userEntityBuilder = userEntity
                    .toBuilder()
                    .password(passwordEncode(request.password()))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();

        var user = userRepository.save(userEntityBuilder);

        var refreshToken = generateRefreshToken();
        var accessToken = jwtUtil.generateAccessToken(user);

        redisTemplate.opsForValue().set("refreshToken", String.valueOf(user.getId()), Duration.ofDays(7));

        return userMapper.toUserSignUpResponseDto(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public userSignUpRequestResponse oauthSignUp(oauth2SignUpRequest oauthDto) {
        if (oauthRepository.existsByOauthRegistrationIdAndOauthUserId(oauthDto.registrationId(), oauthDto.oauthUserId())){
            throw new RuntimeException(HttpStatus.BAD_REQUEST.value() + " 이미 가입된 계정 입니다.");
        }

        var oauthEntity = oauth2Mapper.toEntity(oauthDto);
        oauthRepository.save(oauthEntity);

        UserEntity newUser = UserEntity.builder()
                                .email(oauthDto.email())
                                .nickName(oauthDto.nickname())
                                .password(passwordEncode(UUID.randomUUID().toString().substring(0, 8))) // 랜덤 값
                                .status(UserStatus.ACTIVE)
                                .role(UserRole.USER)
                                .build();

        var user = userRepository.save(newUser);
        var refreshToken = generateRefreshToken();
        var accessToken = jwtUtil.generateAccessToken(user);

        redisTemplate.opsForValue().set("refreshToken", String.valueOf(user.getId()), Duration.ofDays(7));

        return userMapper.toUserSignUpResponseDto(accessToken, refreshToken);
    }
}
