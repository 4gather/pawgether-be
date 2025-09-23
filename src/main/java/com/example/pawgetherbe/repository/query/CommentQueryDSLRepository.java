package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.domain.entity.QCommentEntity;
import com.example.pawgetherbe.domain.entity.QLikeEntity;
import com.example.pawgetherbe.domain.status.CommentStatus;
import com.example.pawgetherbe.mapper.query.CommentQueryMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QCommentEntity commentEntity = QCommentEntity.commentEntity;
    private final QLikeEntity likeEntity = QLikeEntity.likeEntity;
    private final CommentQueryMapper commentQueryMapper;

    @Transactional(readOnly = true)
    public ReadCommentResponse readComments(Long id, long cursor) {
        List<Tuple> comments = jpaQueryFactory
                .select(commentEntity,likeEntity.id.count())
                .from(commentEntity)
                .leftJoin(likeEntity).on(
                        commentEntity.id.eq(likeEntity.targetId),
                        likeEntity.targetType.eq("comment")
                )
                .where(
                        commentEntity.status.eq(CommentStatus.ACTIVE),
                        commentEntity.petFair.id.eq(id),
                        cursorCondition(cursor)
                )
                .groupBy(commentEntity.id)
                .orderBy(commentEntity.createdAt.asc(), commentEntity.id.asc())
                .limit(11)
                .fetch();

        var commentList = comments.stream()
                .map(t -> commentQueryMapper.toReadCommentDto(
                        t.get(commentEntity),
                        t.get(likeEntity.id.count()).intValue()
                ))
                .toList();

        boolean hasMore = commentList.size() == 11;
        long nextCursor = hasMore ? commentList.get(10).commentId() : 0;
        if (commentList.size() == 11) {
            commentList = commentList.subList(0, 10);
        }
        return commentQueryMapper.toReadCommentResponse(commentList, hasMore, nextCursor);
    }

    private BooleanExpression cursorCondition(long cursor) {
        if (cursor > 0) {
            return commentEntity.id.goe(cursor);
        }
        return null;
    }
}
