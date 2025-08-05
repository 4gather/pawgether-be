package com.example.pawgetherbe.usecase.users;

import com.example.pawgetherbe.controller.dto.UserDto;
import com.example.pawgetherbe.controller.dto.UserDto.UpdateUserResponse;

public interface EditUserUseCase {
    UpdateUserResponse updateUserInfo(UserDto.UpdateUserRequest request, String accessHeader);
}
