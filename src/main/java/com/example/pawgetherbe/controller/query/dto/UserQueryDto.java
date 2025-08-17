package com.example.pawgetherbe.controller.query.dto;

public final class UserQueryDto {

    public record EmailCheckRequest(
            String email
    ) {}

    public record NickNameCheckRequest(
            String nickName
    ) {}
}
