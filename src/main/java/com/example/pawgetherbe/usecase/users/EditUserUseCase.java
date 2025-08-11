package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto.UpdateUserRequest;
import com.example.pawgetherbe.controller.dto.UserDto.UpdateUserResponse;

public interface EditUserUseCase {
    UpdateUserResponse updateUserInfo(UpdateUserRequest request);
}
