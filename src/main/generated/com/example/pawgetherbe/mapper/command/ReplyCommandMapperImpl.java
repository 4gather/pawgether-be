package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import com.example.pawgetherbe.domain.entity.ReplyEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-12T23:33:27+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class ReplyCommandMapperImpl implements ReplyCommandMapper {

    @Override
    public ReplyEntity toCreateEntity(ReplyCommandDto.ReplyCreateRequest replyCreateRequest) {
        if ( replyCreateRequest == null ) {
            return null;
        }

        ReplyEntity.ReplyEntityBuilder replyEntity = ReplyEntity.builder();

        replyEntity.content( replyCreateRequest.content() );

        return replyEntity.build();
    }

    @Override
    public ReplyCommandDto.ReplyCreateResponse toReplyCreateResponse(ReplyEntity replyEntity) {
        if ( replyEntity == null ) {
            return null;
        }

        long replyId = 0L;
        long userId = 0L;
        long commentId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;

        if ( replyEntity.getId() != null ) {
            replyId = replyEntity.getId();
        }
        Long id = replyEntityUserId( replyEntity );
        if ( id != null ) {
            userId = id;
        }
        Long id1 = replyEntityCommentId( replyEntity );
        if ( id1 != null ) {
            commentId = id1;
        }
        content = replyEntity.getContent();
        if ( replyEntity.getCreatedAt() != null ) {
            createdAt = replyEntity.getCreatedAt().toString();
        }
        if ( replyEntity.getUpdatedAt() != null ) {
            updatedAt = replyEntity.getUpdatedAt().toString();
        }

        int heart = 0;

        ReplyCommandDto.ReplyCreateResponse replyCreateResponse = new ReplyCommandDto.ReplyCreateResponse( replyId, commentId, userId, content, createdAt, updatedAt, heart );

        return replyCreateResponse;
    }

    @Override
    public ReplyCommandDto.ReplyUpdateResponse toReplyUpdateResponse(ReplyEntity replyEntity) {
        if ( replyEntity == null ) {
            return null;
        }

        long replyId = 0L;
        long userId = 0L;
        long commentId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;

        if ( replyEntity.getId() != null ) {
            replyId = replyEntity.getId();
        }
        Long id = replyEntityUserId( replyEntity );
        if ( id != null ) {
            userId = id;
        }
        Long id1 = replyEntityCommentId( replyEntity );
        if ( id1 != null ) {
            commentId = id1;
        }
        content = replyEntity.getContent();
        if ( replyEntity.getCreatedAt() != null ) {
            createdAt = replyEntity.getCreatedAt().toString();
        }
        if ( replyEntity.getUpdatedAt() != null ) {
            updatedAt = replyEntity.getUpdatedAt().toString();
        }

        int heart = 0;

        ReplyCommandDto.ReplyUpdateResponse replyUpdateResponse = new ReplyCommandDto.ReplyUpdateResponse( replyId, commentId, userId, content, createdAt, updatedAt, heart );

        return replyUpdateResponse;
    }

    private Long replyEntityUserId(ReplyEntity replyEntity) {
        UserEntity user = replyEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private Long replyEntityCommentId(ReplyEntity replyEntity) {
        CommentEntity comment = replyEntity.getComment();
        if ( comment == null ) {
            return null;
        }
        return comment.getId();
    }
}
