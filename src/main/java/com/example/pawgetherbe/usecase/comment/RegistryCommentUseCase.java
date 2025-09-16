package com.example.pawgetherbe.usecase.comment;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;

public interface RegistryCommentUseCase {
    CommentCreateResponse createComment(long petfairId, CommentCreateRequest request);
    CommentUpdateResponse updateComment(long petfairId, long commentId, CommentCommandDto.CommentUpdateRequest request);
}
