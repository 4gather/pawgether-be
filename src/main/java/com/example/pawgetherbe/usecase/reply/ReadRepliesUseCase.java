package com.example.pawgetherbe.usecase.reply;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyCountResponse;
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;

import java.util.List;

public interface ReadRepliesUseCase {
    ReplyReadResponse readReplies(long commentId, long cursor);
    ReplyCountResponse replyCountResponse(List<Long> commentIdList);
}
