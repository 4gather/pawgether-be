package com.example.pawgetherbe.usecase.reply;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;

public interface ReadRepliesUseCase {
    ReplyReadResponse readReplies(long commentId, long cursor);
}
