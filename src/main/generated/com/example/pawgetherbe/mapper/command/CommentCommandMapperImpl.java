package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T23:23:16+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class CommentCommandMapperImpl implements CommentCommandMapper {

    @Override
    public CommentEntity toCreateEntity(CommentCommandDto.CommentCreateRequest commentCreateRequest) {
        if ( commentCreateRequest == null ) {
            return null;
        }

        CommentEntity.CommentEntityBuilder commentEntity = CommentEntity.builder();

        commentEntity.content( commentCreateRequest.content() );

        return commentEntity.build();
    }

    @Override
    public CommentCommandDto.CommentCreateResponse toCreateResponse(CommentEntity commentEntity) {
        if ( commentEntity == null ) {
            return null;
        }

        long commentId = 0L;
        long userId = 0L;
        long petFairId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;

        if ( commentEntity.getId() != null ) {
            commentId = commentEntity.getId();
        }
        Long id = commentEntityUserId( commentEntity );
        if ( id != null ) {
            userId = id;
        }
        Long id1 = commentEntityPetFairId( commentEntity );
        if ( id1 != null ) {
            petFairId = id1;
        }
        content = commentEntity.getContent();
        if ( commentEntity.getCreatedAt() != null ) {
            createdAt = commentEntity.getCreatedAt().toString();
        }
        if ( commentEntity.getUpdatedAt() != null ) {
            updatedAt = commentEntity.getUpdatedAt().toString();
        }

        CommentCommandDto.CommentCreateResponse commentCreateResponse = new CommentCommandDto.CommentCreateResponse( commentId, userId, petFairId, content, createdAt, updatedAt );

        return commentCreateResponse;
    }

    @Override
    public CommentCommandDto.CommentUpdateResponse toCommentUpdateResponse(CommentEntity commentEntity) {
        if ( commentEntity == null ) {
            return null;
        }

        long commentId = 0L;
        long userId = 0L;
        long petFairId = 0L;
        String content = null;
        String createdAt = null;
        String updatedAt = null;

        if ( commentEntity.getId() != null ) {
            commentId = commentEntity.getId();
        }
        Long id = commentEntityUserId( commentEntity );
        if ( id != null ) {
            userId = id;
        }
        Long id1 = commentEntityPetFairId( commentEntity );
        if ( id1 != null ) {
            petFairId = id1;
        }
        content = commentEntity.getContent();
        if ( commentEntity.getCreatedAt() != null ) {
            createdAt = commentEntity.getCreatedAt().toString();
        }
        if ( commentEntity.getUpdatedAt() != null ) {
            updatedAt = commentEntity.getUpdatedAt().toString();
        }

        CommentCommandDto.CommentUpdateResponse commentUpdateResponse = new CommentCommandDto.CommentUpdateResponse( commentId, userId, petFairId, content, createdAt, updatedAt );

        return commentUpdateResponse;
    }

    private Long commentEntityUserId(CommentEntity commentEntity) {
        UserEntity user = commentEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private Long commentEntityPetFairId(CommentEntity commentEntity) {
        PetFairEntity petFair = commentEntity.getPetFair();
        if ( petFair == null ) {
            return null;
        }
        return petFair.getId();
    }
}
