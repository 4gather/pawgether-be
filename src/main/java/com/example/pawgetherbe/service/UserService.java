package com.example.pawgetherbe.service;

import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequestResponse;
import com.example.pawgetherbe.controller.dto.UserDto.userSignUpRequest;
import com.example.pawgetherbe.domain.status.UserRole;
import com.example.pawgetherbe.domain.status.UserStatus;
import com.example.pawgetherbe.mapper.UserMapper;
import com.example.pawgetherbe.repository.UserRepository;
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import static com.example.pawgetherbe.util.EncryptUtil.generateRefreshToken;
import static com.example.pawgetherbe.util.EncryptUtil.passwordEncode;

@Service
@RequiredArgsConstructor
public class UserService implements SignUpWithIdUseCase {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public userSignUpRequestResponse signUp(userSignUpRequest request) {
        var userEntity = userMapper.toUserEntity(request);
        var userEntityBuilder = userEntity
                    .toBuilder()
                    .password(passwordEncode(request.password()))
                    .status(UserStatus.ACTIVE)
                    .role(UserRole.USER)
                    .build();
        var user = userRepository.save(userEntityBuilder);

        var refreshToken = generateRefreshToken();

        redisTemplate.opsForValue().set("refreshToken", String.valueOf(user.getId()));

        return userMapper.toUserSignUpResponseDto(user, refreshToken);
    }
}
