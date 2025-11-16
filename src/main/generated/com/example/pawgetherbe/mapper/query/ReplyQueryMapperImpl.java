package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import com.example.pawgetherbe.domain.entity.ReplyEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T23:23:16+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class ReplyQueryMapperImpl implements ReplyQueryMapper {

    @Override
    public ReplyQueryDto.ReplyReadDto toReplyReadDto(ReplyEntity replyEntity) {
        if ( replyEntity == null ) {
            return null;
        }

        long replyId = 0L;
        String nickName = null;
        long commentId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;

        if ( replyEntity.getId() != null ) {
            replyId = replyEntity.getId();
        }
        nickName = replyEntityUserNickName( replyEntity );
        Long id = replyEntityCommentId( replyEntity );
        if ( id != null ) {
            commentId = id;
        }
        content = replyEntity.getContent();
        if ( replyEntity.getCreatedAt() != null ) {
            createdAt = replyEntity.getCreatedAt().toString();
        }
        if ( replyEntity.getUpdatedAt() != null ) {
            updatedAt = replyEntity.getUpdatedAt().toString();
        }

        ReplyQueryDto.ReplyReadDto replyReadDto = new ReplyQueryDto.ReplyReadDto( replyId, commentId, nickName, content, createdAt, updatedAt );

        return replyReadDto;
    }

    @Override
    public ReplyQueryDto.ReplyReadResponse toReplyReadResponse(List<ReplyQueryDto.ReplyReadDto> replies, boolean hasMore, long nextCursor) {
        if ( replies == null ) {
            return null;
        }

        List<ReplyQueryDto.ReplyReadDto> replies1 = null;
        List<ReplyQueryDto.ReplyReadDto> list = replies;
        if ( list != null ) {
            replies1 = new ArrayList<ReplyQueryDto.ReplyReadDto>( list );
        }
        boolean hasMore1 = false;
        hasMore1 = hasMore;
        long nextCursor1 = 0L;
        nextCursor1 = nextCursor;

        ReplyQueryDto.ReplyReadResponse replyReadResponse = new ReplyQueryDto.ReplyReadResponse( hasMore1, nextCursor1, replies1 );

        return replyReadResponse;
    }

    private String replyEntityUserNickName(ReplyEntity replyEntity) {
        UserEntity user = replyEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getNickName();
    }

    private Long replyEntityCommentId(ReplyEntity replyEntity) {
        CommentEntity comment = replyEntity.getComment();
        if ( comment == null ) {
            return null;
        }
        return comment.getId();
    }
}
