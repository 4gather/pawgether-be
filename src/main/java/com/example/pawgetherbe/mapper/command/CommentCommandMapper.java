package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentUpdateResponse;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateRequest;
import com.example.pawgetherbe.controller.command.dto.CommentCommandDto.CommentCreateResponse;
import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CommentCommandMapper {
    CommentEntity toCreateEntity(CommentCreateRequest commentCreateRequest);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "petFairId", source = "petFair.id")
    CommentCreateResponse toCreateResponse(CommentEntity commentEntity);

    @Mapping(target = "commentId", source = "id")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "petFairId", source = "petFair.id")
    CommentUpdateResponse toCommentUpdateResponse(CommentEntity commentEntity);
}
