package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.common.exceptionHandler.CustomException;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairPosterDto;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.QPetFairEntity;
import com.example.pawgetherbe.domain.status.PetFairFilterStatus;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.example.pawgetherbe.exception.query.PetFairQueryErrorCode.EMPTY_PET_FAIR_FILTER_STATUS;

@Repository
@RequiredArgsConstructor
public class PetFairQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QPetFairEntity petFair = QPetFairEntity.petFairEntity;

    @Transactional(readOnly = true)
    public List<PetFairPosterDto> petFairCarousel() {
        var today = LocalDate.now();

        return jpaQueryFactory
                .select(petFair.id, petFair.posterImageUrl)
                .from(petFair)
                .where(
                        petFair.endDate.goe(today)
                                .and(petFair.status.eq(PetFairStatus.ACTIVE))
                )
                .orderBy(petFair.startDate.desc())
                .limit(10)
                .fetch()
                .stream()
                .map(t -> new PetFairPosterDto(t.get(petFair.id), t.get(petFair.posterImageUrl)))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PetFairQueryDto.PetFairCalendarDto> petFairCalendar(String date) {
        var CalendarDate = LocalDate.parse(date);
        LocalDate startDate = CalendarDate.minusMonths(6);
        LocalDate endDate   = CalendarDate.plusMonths(6);

        return jpaQueryFactory
                .select(Projections.constructor(
                        PetFairQueryDto.PetFairCalendarDto.class,
                        petFair.id,                                  // Long petFairId
                        petFair.counter.coalesce(0L),               // Long counter (NULL 방지)
                        petFair.title,                              // String title
                        petFair.posterImageUrl,                     // String posterImageUrl
                        petFair.startDate,                          // LocalDate startDate
                        petFair.endDate,                            // LocalDate endDate
                        petFair.simpleAddress
                ))
                .from(petFair)
                .where(
                        petFair.status.eq(PetFairStatus.ACTIVE),
                        petFair.startDate.loe(endDate),           // 시작일이 윈도우 끝보다 같거나 이른 것
                        petFair.endDate.goe(startDate)            // 종료일이 윈도우 시작보다 같거나 늦은 것
                )
                .orderBy(petFair.startDate.desc())
                .fetch();
    }

    // Post Status 가 ACTIVE 인 것만 조회
    @Transactional(readOnly = true)
    public Optional<PetFairEntity> findActiveById(Long petFairId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(petFair)
                .where(
                        petFair.id.eq(petFairId),
                        petFair.status.eq(PetFairStatus.ACTIVE)
                )
                .from(petFair)
                .fetchOne()
        );
    }

    // Post Status  == ACTIVE 조회/ Order By[startDate,id] == Desc 정렬
    @Transactional(readOnly = true)
    public List<PetFairEntity> findActiveListOrderByDesc() {
        return jpaQueryFactory
                .selectFrom(petFair)
                .where(
                        petFair.status.eq(PetFairStatus.ACTIVE)
                )
                .orderBy(petFair.startDate.desc(), petFair.id.desc())
                .limit(11) // hasMore 계산을 위해 10+1개 가져옴
                .fetch();
    }

    // ACTIVE 게시글의 조건별 count 동적 쿼리
    @Transactional(readOnly = true)
    public Long countActiveByStatus(PetFairFilterStatus status) {

        return jpaQueryFactory
                .select(petFair.count())
                .where(
                        petFair.status.eq(PetFairStatus.ACTIVE),
                        filterByStatus(status)
                )
                .from(petFair)
                .orderBy(getOrderBy(status))
                .fetchOne();
    }

    // PetFairFilterStatus 에 따른 Where 절
    private BooleanExpression filterByStatus(PetFairFilterStatus status) {
        LocalDate now = LocalDate.now();

        if (status == null) {
            throw new CustomException(EMPTY_PET_FAIR_FILTER_STATUS);
        }

        return switch(status) {
            case PetFairFilterStatus.PET_FAIR_ALL -> null; // null은 무시되므로 where에서 ACTIVE 조건만 적용
            case PetFairFilterStatus.PET_FAIR_ACTIVE -> petFair.endDate.goe(now); // 박람회 종료날짜 >= 오늘
            case PetFairFilterStatus.PET_FAIR_FINISHED -> petFair.endDate.lt(now); // 박람회 종료 날짜 < 오늘
        };
    }

    // PetFairFilterStatus 에 따른 OrderBy 조건문
    private OrderSpecifier getOrderBy(PetFairFilterStatus status) {
        if (status == null) {
            throw new CustomException(EMPTY_PET_FAIR_FILTER_STATUS);
        }

        return switch (status) {
            case PetFairFilterStatus.PET_FAIR_ALL -> petFair.startDate.desc();
            case PetFairFilterStatus.PET_FAIR_ACTIVE -> petFair.startDate.asc();
            case PetFairFilterStatus.PET_FAIR_FINISHED -> petFair.startDate.desc();
        };
    }
}
