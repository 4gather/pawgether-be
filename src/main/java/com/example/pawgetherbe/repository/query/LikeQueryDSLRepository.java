package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.CountLikeResponse;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.TargetRequest;
import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.TargetRequests;
import com.example.pawgetherbe.domain.UserContext;
import com.example.pawgetherbe.domain.entity.LikeEntity;
import com.example.pawgetherbe.domain.entity.QLikeEntity;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class LikeQueryDSLRepository {

    private final JPAQueryFactory jpaQueryFactory;
    private final QLikeEntity likeEntity = QLikeEntity.likeEntity;

    @Transactional(readOnly = true)
    public boolean hasUserLikedTarget(TargetRequest targetRequest) {
        return jpaQueryFactory.selectOne()
                .from(likeEntity)
                .where(
                        // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                        likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                        likeEntity.user.id.eq(1L),
                        likeEntity.targetType.eq(targetRequest.targetType()),
                        likeEntity.targetId.eq(targetRequest.targetId())
                )
                .fetchFirst() != null;
    }

    @Transactional(readOnly =true)
    public Long existsLikedByUserAndTarget(TargetRequest target) {
        return jpaQueryFactory
                .select(likeEntity.targetId)
                .from(likeEntity)
                .where(
                        likeEntity.user.id.eq(1L),
                        // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
//                        likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId()))
                        likeEntity.targetType.eq(target.targetType()),
                        likeEntity.targetId.eq(target.targetId())
                        )
                .fetchOne();
    }

    @Transactional(readOnly =true)
    public Set<Long> existsLikedByUserAndTargetList(TargetRequests targets) {
        return new HashSet<>(
                jpaQueryFactory
                        .select(likeEntity.targetId)
                        .from(likeEntity)
                        .where(
                                likeEntity.user.id.eq(1L),
                                // TODO: Filter에서 UserContext 사용 시 주석 코드 사용
//                                likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId()))
                                likeEntity.targetType.eq(targets.targetType()),
                                likeEntity.targetId.in(targets.targetIds())
                        )
                        .fetch()
        );
    }

    @Transactional(readOnly = true)
    public Optional<LikeEntity> findLikeByUserAndTarget(LikeRequest likeRequest) {
        return Optional.ofNullable(
                jpaQueryFactory.selectFrom(likeEntity)
                        .where(
                                // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                                likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId())),
                                likeEntity.user.id.eq(1L),
                                likeEntity.targetType.eq(likeRequest.targetType()),
                                likeEntity.targetId.eq(likeRequest.targetId())
                        )
                        .fetchOne()
        );
    }

    @Transactional(readOnly = true)
    public long countLikeByTarget(TargetRequest targetRequest) {
        Long count = jpaQueryFactory
                .select(likeEntity.count())
                .from(likeEntity)
                .where(
                        likeEntity.targetType.eq(targetRequest.targetType()),
                        likeEntity.targetId.eq(targetRequest.targetId())
                )
                .groupBy(likeEntity.targetId)
                .fetchOne();

        return (count != null) ? count : 0L;
    }

    @Transactional(readOnly = true)
    public Set<CountLikeResponse> countLikeByTargetList(TargetRequests targetRequests) {
        Set<Tuple> counts = new HashSet<>(
                jpaQueryFactory
                .select(likeEntity.targetId, likeEntity.count())
                .from(likeEntity)
                .where(
                        likeEntity.targetType.eq(targetRequests.targetType()),
                        likeEntity.targetId.in(targetRequests.targetIds())
                )
                .groupBy(likeEntity.targetId)
                .fetch()
        );

        return counts.stream()
                .map(tuple -> new CountLikeResponse(
                        targetRequests.targetType(),
                        tuple.get(likeEntity.targetId),
                        tuple.get(likeEntity.count()) != null ? tuple.get(likeEntity.count()) : 0L
                ))
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<LikeEntity> findLikesByUser() {
        return jpaQueryFactory
                .selectFrom(likeEntity)
                .where(
                        // 실제 서비스에서는 Long.parseLong(UserContext.getUserId()) 사용
//                        likeEntity.user.id.eq(Long.parseLong(UserContext.getUserId()))
                        likeEntity.user.id.eq(1L)
                )
                .orderBy(likeEntity.createdAt.desc())  // 최신 좋아요 순으로 정렬
                .fetch();
    }
}
