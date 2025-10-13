package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-12T23:33:27+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class CommentQueryMapperImpl implements CommentQueryMapper {

    @Override
    public CommentQueryDto.ReadCommentDto toReadCommentDto(CommentEntity comment, int count) {
        if ( comment == null ) {
            return null;
        }

        long commentId = 0L;
        String nickName = null;
        long petFairId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;
        if ( comment != null ) {
            if ( comment.getId() != null ) {
                commentId = comment.getId();
            }
            nickName = commentUserNickName( comment );
            Long id = commentPetFairId( comment );
            if ( id != null ) {
                petFairId = id;
            }
            content = comment.getContent();
            if ( comment.getCreatedAt() != null ) {
                createdAt = comment.getCreatedAt().toString();
            }
            if ( comment.getUpdatedAt() != null ) {
                updatedAt = comment.getUpdatedAt().toString();
            }
        }
        int heart = 0;
        heart = count;

        CommentQueryDto.ReadCommentDto readCommentDto = new CommentQueryDto.ReadCommentDto( commentId, petFairId, nickName, content, createdAt, updatedAt, heart );

        return readCommentDto;
    }

    @Override
    public CommentQueryDto.ReadCommentResponse toReadCommentResponse(List<CommentQueryDto.ReadCommentDto> comments, boolean hasMore, long nextCursor) {
        if ( comments == null ) {
            return null;
        }

        List<CommentQueryDto.ReadCommentDto> comments1 = null;
        List<CommentQueryDto.ReadCommentDto> list = comments;
        if ( list != null ) {
            comments1 = new ArrayList<CommentQueryDto.ReadCommentDto>( list );
        }
        boolean hasMore1 = false;
        hasMore1 = hasMore;
        long nextCursor1 = 0L;
        nextCursor1 = nextCursor;

        CommentQueryDto.ReadCommentResponse readCommentResponse = new CommentQueryDto.ReadCommentResponse( hasMore1, nextCursor1, comments1 );

        return readCommentResponse;
    }

    private String commentUserNickName(CommentEntity commentEntity) {
        UserEntity user = commentEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getNickName();
    }

    private Long commentPetFairId(CommentEntity commentEntity) {
        PetFairEntity petFair = commentEntity.getPetFair();
        if ( petFair == null ) {
            return null;
        }
        return petFair.getId();
    }
}
