package com.example.pawgetherbe.usecase.comment;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;

public interface RegistryCommentUseCase {
    CommentCreateResponse createComment(long petfairId, CommentCreateRequest request);
}
