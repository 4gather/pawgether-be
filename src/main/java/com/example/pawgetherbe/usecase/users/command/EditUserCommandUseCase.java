package com.example.pawgetherbe.usecase.users.command;

import com.example.pawgetherbe.controller.command.dto.UserCommandDto.PasswordEditRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UpdateUserResponse;

public interface EditUserCommandUseCase {
    UpdateUserResponse updateUserInfo(UpdateUserRequest request);
    void updatePassword(PasswordEditRequest passwordEditRequest);
}
