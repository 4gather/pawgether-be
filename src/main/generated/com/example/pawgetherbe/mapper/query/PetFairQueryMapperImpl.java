package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.PetFairQueryDto;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import com.example.pawgetherbe.domain.status.PetFairStatus;
import java.time.Instant;
import java.time.LocalDate;
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
public class PetFairQueryMapperImpl implements PetFairQueryMapper {

    @Override
    public PetFairQueryDto.DetailPetFairResponse toDetailPetFair(PetFairEntity petFairEntity) {
        if ( petFairEntity == null ) {
            return null;
        }

        Long petFairId = null;
        Long userId = null;
        List<String> images = null;
        String title = null;
        String posterImageUrl = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String simpleAddress = null;
        String detailAddress = null;
        String petFairUrl = null;
        String content = null;
        Long counter = null;
        String telNumber = null;
        PetFairStatus status = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        petFairId = petFairEntity.getId();
        userId = petFairEntityUserId( petFairEntity );
        images = petFairImageEntityListToStringList( petFairEntity.getPairImages() );
        title = petFairEntity.getTitle();
        posterImageUrl = petFairEntity.getPosterImageUrl();
        startDate = petFairEntity.getStartDate();
        endDate = petFairEntity.getEndDate();
        simpleAddress = petFairEntity.getSimpleAddress();
        detailAddress = petFairEntity.getDetailAddress();
        petFairUrl = petFairEntity.getPetFairUrl();
        content = petFairEntity.getContent();
        counter = petFairEntity.getCounter();
        telNumber = petFairEntity.getTelNumber();
        status = petFairEntity.getStatus();
        createdAt = petFairEntity.getCreatedAt();
        updatedAt = petFairEntity.getUpdatedAt();

        PetFairQueryDto.DetailPetFairResponse detailPetFairResponse = new PetFairQueryDto.DetailPetFairResponse( petFairId, userId, title, posterImageUrl, startDate, endDate, simpleAddress, detailAddress, petFairUrl, content, counter, telNumber, status, createdAt, updatedAt, images );

        return detailPetFairResponse;
    }

    @Override
    public PetFairQueryDto.SummaryPetFairResponse toSummaryPetFair(PetFairEntity entity) {
        if ( entity == null ) {
            return null;
        }

        Long petFairId = null;
        Long counter = null;
        String title = null;
        String posterImageUrl = null;
        LocalDate startDate = null;
        LocalDate endDate = null;
        String simpleAddress = null;

        petFairId = entity.getId();
        counter = entity.getCounter();
        title = entity.getTitle();
        posterImageUrl = entity.getPosterImageUrl();
        startDate = entity.getStartDate();
        endDate = entity.getEndDate();
        simpleAddress = entity.getSimpleAddress();

        PetFairQueryDto.SummaryPetFairResponse summaryPetFairResponse = new PetFairQueryDto.SummaryPetFairResponse( petFairId, counter, title, posterImageUrl, startDate, endDate, simpleAddress );

        return summaryPetFairResponse;
    }

    private Long petFairEntityUserId(PetFairEntity petFairEntity) {
        UserEntity user = petFairEntity.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    protected List<String> petFairImageEntityListToStringList(List<PetFairImageEntity> list) {
        if ( list == null ) {
            return null;
        }

        List<String> list1 = new ArrayList<String>( list.size() );
        for ( PetFairImageEntity petFairImageEntity : list ) {
            list1.add( ImageUrl( petFairImageEntity ) );
        }

        return list1;
    }
}
