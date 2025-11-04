package com.example.pawgetherbe.service.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;
import com.example.pawgetherbe.repository.query.CommentQueryRepository;
import com.example.pawgetherbe.repository.query.ReplyQueryDSLRepository;
import com.example.pawgetherbe.usecase.reply.ReadRepliesUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.example.pawgetherbe.exception.query.CommentQueryErrorCode.NOT_FOUND_COMMENT_CALENDAR;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReplyQueryService implements ReadRepliesUseCase {

    private final ReplyQueryDSLRepository replyQueryDSLRepository;
    private final CommentQueryRepository commentQueryRepository;


    @Override
    public ReplyReadResponse readReplies(long commentId, long cursor) {
        var comment = commentQueryRepository.existsById(commentId);
        if(!comment) {
            throw new CustomException(NOT_FOUND_COMMENT_CALENDAR);
        }

        return replyQueryDSLRepository.readReplies(commentId, cursor);
    }
}
