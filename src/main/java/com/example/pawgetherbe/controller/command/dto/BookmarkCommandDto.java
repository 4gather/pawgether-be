package com.example.pawgetherbe.controller.command.dto;

public final class BookmarkCommandDto {
    public record BookmarkResponse(
            long targetId,
            boolean isBookmarked
    ) {}
}
