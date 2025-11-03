package com.example.pawgetherbe.usecase.comment;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.CommentCountResponse;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.MainCommentResponse;
import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;

public interface ReadCommentsUseCase {
    ReadCommentResponse readComments(long petfairId, long cursor);
    MainCommentResponse mainComments();
    CommentCountResponse commentCount(long petfairId);
}
