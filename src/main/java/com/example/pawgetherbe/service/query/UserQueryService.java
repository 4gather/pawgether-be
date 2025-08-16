package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.AccessTokenStatus;
import com.example.pawgetherbe.mapper.query.UserQueryMapper;
import com.example.pawgetherbe.repository.query.UserQueryRepository;
import com.example.pawgetherbe.usecase.jwt.query.RefreshQueryUseCase;
import com.example.pawgetherbe.usecase.users.query.SignUpQueryUseCase;
import com.example.pawgetherbe.util.EncryptUtil;
import com.example.pawgetherbe.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static com.example.pawgetherbe.common.filter.JwtAuthFilter.*;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements RefreshQueryUseCase, SignUpQueryUseCase {

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtUtil jwtUtil;
    private final UserQueryRepository userQueryRepository;
    private final UserQueryMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public void signupEmailCheck(String email) {
        if (userQueryRepository.existsByEmail(email)) {
            throw new CustomException(CONFLICT_EMAIL);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void signupNicknameCheck(String nickName) {
        if(userQueryRepository.existsByNickName(nickName)) {
            throw new CustomException(CONFLICT_NICKNAME);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, String> refresh(String authHeader, String refreshToken) {

        if (!authHeader.startsWith(AUTH_BEARER)) {
            throw new CustomException(UNAUTHORIZED_LOGIN);
        }

        String accessToken = authHeader.substring(AUTH_BEARER.length());

        if (jwtUtil.validateToken(accessToken).equals(AccessTokenStatus.INVALID)) {
            throw new CustomException(UNAUTHORIZED_LOGIN);
        }

        // case1] refresh token 만료: 재로그인
        if (!isValidRefreshToken(refreshToken)) {
            throw new CustomException(UNAUTHORIZED_LOGIN);
        }

        Long userId = jwtUtil.getUserIdFromToken(accessToken);
        String userRole = jwtUtil.getUserRoleFromToken(accessToken);

        // case2] refresh token 만료 X: 갱신 로직
        UserEntity user= userQueryRepository.findById(userId).orElseThrow(
                () -> new CustomException(NOT_FOUND_USER)
        );

        String renewAccessToken = jwtUtil.generateAccessToken(userMapper.toAccessTokenDto(userId, userRole));
        String renewRefreshToken = EncryptUtil.generateRefreshToken();

        return Map.of(
                "accessToken", renewAccessToken,
                "refreshToken", renewRefreshToken
        );
    }

    private boolean isValidRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken) != null;
    }
}
