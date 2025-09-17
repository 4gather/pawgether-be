package com.example.pawgetherbe.usecase.comment;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateRequest;

public interface EditCommentUseCase {
    CommentUpdateResponse updateComment(long petfairId, long commentId, CommentUpdateRequest request);
}
