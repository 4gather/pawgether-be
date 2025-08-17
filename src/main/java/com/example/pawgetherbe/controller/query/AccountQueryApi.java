package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.UserQueryDto.EmailCheckRequest;
import com.example.pawgetherbe.controller.query.dto.UserQueryDto.NickNameCheckRequest;
import com.example.pawgetherbe.usecase.users.query.SignUpQueryUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static com.example.pawgetherbe.exception.query.UserQueryErrorCode.INVALID_FORMAT_EMAIL;
import static com.example.pawgetherbe.exception.query.UserQueryErrorCode.INVALID_FORMAT_NICKNAME;
import static com.example.pawgetherbe.util.ValidationUtil.isValidEmail;
import static com.example.pawgetherbe.util.ValidationUtil.isValidNickName;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class AccountQueryApi {

    private final SignUpQueryUseCase signUpQueryUseCase;

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
}
