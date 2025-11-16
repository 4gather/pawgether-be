package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.controller.command.dto.PetFairCommandDto;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-11-15T23:23:16+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class PetFairCommandMapperImpl implements PetFairCommandMapper {

    @Override
    public PetFairEntity toPetFairEntity(PetFairCommandDto.PetFairCreateRequest petFairCreateRequest) {
        if ( petFairCreateRequest == null ) {
            return null;
        }

        PetFairEntity.PetFairEntityBuilder petFairEntity = PetFairEntity.builder();

        petFairEntity.title( petFairCreateRequest.title() );
        if ( petFairCreateRequest.startDate() != null ) {
            petFairEntity.startDate( LocalDate.parse( petFairCreateRequest.startDate() ) );
        }
        if ( petFairCreateRequest.endDate() != null ) {
            petFairEntity.endDate( LocalDate.parse( petFairCreateRequest.endDate() ) );
        }
        petFairEntity.simpleAddress( petFairCreateRequest.simpleAddress() );
        petFairEntity.detailAddress( petFairCreateRequest.detailAddress() );
        petFairEntity.petFairUrl( petFairCreateRequest.petFairUrl() );
        petFairEntity.content( petFairCreateRequest.content() );
        petFairEntity.telNumber( petFairCreateRequest.telNumber() );

        return petFairEntity.build();
    }

    @Override
    public PetFairEntity toPetFairEntity(PetFairCommandDto.UpdatePetFairRequest updatePetFairRequest) {
        if ( updatePetFairRequest == null ) {
            return null;
        }

        PetFairEntity.PetFairEntityBuilder petFairEntity = PetFairEntity.builder();

        petFairEntity.title( updatePetFairRequest.title() );
        if ( updatePetFairRequest.startDate() != null ) {
            petFairEntity.startDate( LocalDate.parse( updatePetFairRequest.startDate() ) );
        }
        if ( updatePetFairRequest.endDate() != null ) {
            petFairEntity.endDate( LocalDate.parse( updatePetFairRequest.endDate() ) );
        }
        petFairEntity.simpleAddress( updatePetFairRequest.simpleAddress() );
        petFairEntity.detailAddress( updatePetFairRequest.detailAddress() );
        petFairEntity.petFairUrl( updatePetFairRequest.petFairUrl() );
        petFairEntity.content( updatePetFairRequest.content() );
        petFairEntity.telNumber( updatePetFairRequest.telNumber() );

        return petFairEntity.build();
    }

    @Override
    public PetFairCommandDto.PetFairCreateResponse toPetFairCreateResponse(PetFairEntity petFairEntity) {
        if ( petFairEntity == null ) {
            return null;
        }

        Long petFairId = null;
        List<String> images = null;
        String title = null;
        String content = null;
        String posterImageUrl = null;
        String petFairUrl = null;
        String simpleAddress = null;
        String detailAddress = null;
        String startDate = null;
        String endDate = null;
        String telNumber = null;
        Long counter = null;
        String createdAt = null;
        String updatedAt = null;

        petFairId = petFairEntity.getId();
        images = mapImages( petFairEntity.getPairImages() );
        title = petFairEntity.getTitle();
        content = petFairEntity.getContent();
        posterImageUrl = petFairEntity.getPosterImageUrl();
        petFairUrl = petFairEntity.getPetFairUrl();
        simpleAddress = petFairEntity.getSimpleAddress();
        detailAddress = petFairEntity.getDetailAddress();
        if ( petFairEntity.getStartDate() != null ) {
            startDate = DateTimeFormatter.ISO_LOCAL_DATE.format( petFairEntity.getStartDate() );
        }
        if ( petFairEntity.getEndDate() != null ) {
            endDate = DateTimeFormatter.ISO_LOCAL_DATE.format( petFairEntity.getEndDate() );
        }
        telNumber = petFairEntity.getTelNumber();
        counter = petFairEntity.getCounter();
        if ( petFairEntity.getCreatedAt() != null ) {
            createdAt = petFairEntity.getCreatedAt().toString();
        }
        if ( petFairEntity.getUpdatedAt() != null ) {
            updatedAt = petFairEntity.getUpdatedAt().toString();
        }

        PetFairCommandDto.PetFairCreateResponse petFairCreateResponse = new PetFairCommandDto.PetFairCreateResponse( petFairId, title, content, posterImageUrl, petFairUrl, simpleAddress, detailAddress, startDate, endDate, telNumber, images, counter, createdAt, updatedAt );

        return petFairCreateResponse;
    }

    @Override
    public PetFairCommandDto.UpdatePetFairResponse toUpdatePetFairResponse(PetFairEntity petFairEntity) {
        if ( petFairEntity == null ) {
            return null;
        }

        Long petFairId = null;
        List<String> images = null;
        String title = null;
        String content = null;
        String posterImageUrl = null;
        String petFairUrl = null;
        String simpleAddress = null;
        String detailAddress = null;
        String startDate = null;
        String endDate = null;
        String telNumber = null;
        Long counter = null;
        String createdAt = null;
        String updatedAt = null;

        petFairId = petFairEntity.getId();
        images = mapImages( petFairEntity.getPairImages() );
        title = petFairEntity.getTitle();
        content = petFairEntity.getContent();
        posterImageUrl = petFairEntity.getPosterImageUrl();
        petFairUrl = petFairEntity.getPetFairUrl();
        simpleAddress = petFairEntity.getSimpleAddress();
        detailAddress = petFairEntity.getDetailAddress();
        if ( petFairEntity.getStartDate() != null ) {
            startDate = DateTimeFormatter.ISO_LOCAL_DATE.format( petFairEntity.getStartDate() );
        }
        if ( petFairEntity.getEndDate() != null ) {
            endDate = DateTimeFormatter.ISO_LOCAL_DATE.format( petFairEntity.getEndDate() );
        }
        telNumber = petFairEntity.getTelNumber();
        counter = petFairEntity.getCounter();
        if ( petFairEntity.getCreatedAt() != null ) {
            createdAt = petFairEntity.getCreatedAt().toString();
        }
        if ( petFairEntity.getUpdatedAt() != null ) {
            updatedAt = petFairEntity.getUpdatedAt().toString();
        }

        PetFairCommandDto.UpdatePetFairResponse updatePetFairResponse = new PetFairCommandDto.UpdatePetFairResponse( petFairId, title, content, posterImageUrl, petFairUrl, simpleAddress, detailAddress, startDate, endDate, telNumber, images, counter, createdAt, updatedAt );

        return updatePetFairResponse;
    }
}
