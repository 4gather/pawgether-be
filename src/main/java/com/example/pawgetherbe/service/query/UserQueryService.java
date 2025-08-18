package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.repository.query.UserQueryRepository;
import com.example.pawgetherbe.usecase.users.query.SignUpQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.CONFLICT_EMAIL;
import static com.example.pawgetherbe.exception.command.UserCommandErrorCode.CONFLICT_NICKNAME;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserQueryService implements SignUpQueryUseCase {

    private final UserQueryRepository userQueryRepository;

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
}
