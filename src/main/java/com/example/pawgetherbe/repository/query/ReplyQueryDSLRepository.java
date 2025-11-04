package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto;
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;
import com.example.pawgetherbe.domain.entity.QLikeEntity;
import com.example.pawgetherbe.domain.entity.QReplyEntity;
import com.example.pawgetherbe.domain.status.ReplyStatus;
import com.example.pawgetherbe.mapper.query.ReplyQueryMapper;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.requireNonNullElse;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ReplyQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;
    private final QReplyEntity reply = QReplyEntity.replyEntity;
    private final QLikeEntity likeEntity = QLikeEntity.likeEntity;
    private final ReplyQueryMapper replyQueryMapper;

    @Transactional(readOnly = true)
    public ReplyReadResponse readReplies(Long id, long cursor) {
        List<Tuple> replies = jpaQueryFactory
                .select(reply,likeEntity.id.count())
                .from(reply)
                .leftJoin(likeEntity).on(
                        reply.id.eq(likeEntity.targetId),
                        likeEntity.targetType.eq("reply")
                )
                .where(
                        reply.status.eq(ReplyStatus.ACTIVE),
                        reply.comment.id.eq(id),
                        cursorCondition(cursor)
                )
                .groupBy(reply.id)
                .orderBy(reply.createdAt.asc(), reply.id.asc())
                .limit(11)
                .fetch();

        var replyList = replies.stream()
                .map(t -> replyQueryMapper.toReplyReadDto(
                        t.get(reply),
                        t.get(likeEntity.id.count()).intValue()
                ))
                .toList();

        boolean hasMore = replyList.size() == 11;
        long nextCursor = hasMore ? replyList.get(10).replyId() : 0;
        if (replyList.size() == 11) {
            replyList = replyList.subList(0, 10);
        }
        return replyQueryMapper.toReplyReadResponse(replyList, hasMore, nextCursor);
    }

    @Transactional(readOnly = true)
    public ReplyQueryDto.ReplyCountResponse countReplies(List<Long> commentIdList) {
        List<Tuple> replyCounts = jpaQueryFactory
                .select(reply.comment.id, reply.id.count())
                .from(reply)
                .where(
                        reply.comment.id.in(commentIdList),
                        reply.status.eq(ReplyStatus.ACTIVE)
                )
                .groupBy(reply.comment.id)
                .fetch();

        List<ReplyQueryDto.ReplyCountDto> dtos =
                replyCounts.stream()
                        .map(t -> new ReplyQueryDto.ReplyCountDto(
                                "Comment",
                                requireNonNullElse(t.get(0, Long.class), 0L),
                                requireNonNullElse(t.get(1, Long.class), 0L)
                        ))
                        .collect(Collectors.toList());


        return new ReplyQueryDto.ReplyCountResponse(dtos);
    }

    private BooleanExpression cursorCondition(long cursor) {
        if (cursor > 0) {
            return reply.id.goe(cursor);
        }
        return null;
    }
}
