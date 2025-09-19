package com.example.pawgetherbe.usecase.reply;

import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateResponse;
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateRequest;

public interface EditReplyUseCase {
    ReplyUpdateResponse updateReply(ReplyUpdateRequest request);
}
