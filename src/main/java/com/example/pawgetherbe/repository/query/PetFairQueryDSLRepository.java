package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto.PetFairPosterDto;
import com.example.pawgetherbe.domain.entity.QPetFairEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PetFairQueryDSLRepository {
    private final JPAQueryFactory jpaQueryFactory;

    private final QPetFairEntity petFair = QPetFairEntity.petFairEntity;

    @Transactional(readOnly = true)
    public List<PetFairPosterDto> petFairCarousel() {
        var today = java.time.LocalDate.now();

        return jpaQueryFactory
                .select(petFair.id, petFair.posterImageUrl)
                .from(petFair)
                .where(petFair.endDate.goe(today))
                .orderBy(petFair.startDate.asc(), petFair.id.asc())
                .limit(10)
                .fetch()
                .stream()
                .map(t -> new PetFairPosterDto(t.get(petFair.id), t.get(petFair.posterImageUrl)))
                .toList();
    }
}
