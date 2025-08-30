package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto;
import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairPosterDto;
import com.example.pawgetherbe.domain.entity.QPetFairEntity;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

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
}
