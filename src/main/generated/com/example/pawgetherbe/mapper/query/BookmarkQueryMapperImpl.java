package com.example.pawgetherbe.mapper.query;

import com.example.pawgetherbe.controller.query.dto.BookmarkQueryDto;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import java.time.LocalDate;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-10T15:39:35+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class BookmarkQueryMapperImpl implements BookmarkQueryMapper {

    @Override
    public BookmarkQueryDto.DetailBookmarkedPetFairResponse toDetailBookmarkedPetPairResponse(PetFairEntity entity, boolean isBookmark) {
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
        if ( entity != null ) {
            petFairId = entity.getId();
            counter = entity.getCounter();
            title = entity.getTitle();
            posterImageUrl = entity.getPosterImageUrl();
            startDate = entity.getStartDate();
            endDate = entity.getEndDate();
            simpleAddress = entity.getSimpleAddress();
        }
        boolean isBookmark1 = false;
        isBookmark1 = isBookmark;

        BookmarkQueryDto.DetailBookmarkedPetFairResponse detailBookmarkedPetFairResponse = new BookmarkQueryDto.DetailBookmarkedPetFairResponse( petFairId, counter, title, posterImageUrl, startDate, endDate, simpleAddress, isBookmark1 );

        return detailBookmarkedPetFairResponse;
    }
}
