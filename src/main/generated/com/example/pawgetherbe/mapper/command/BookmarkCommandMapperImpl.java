package com.example.pawgetherbe.mapper.command;

import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-13T15:08:25+0900",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.8 (Amazon.com Inc.)"
)
@Component
public class BookmarkCommandMapperImpl implements BookmarkCommandMapper {

    @Override
    public BookmarkEntity toEntity(UserEntity user, PetFairEntity petFair) {
        if ( user == null && petFair == null ) {
            return null;
        }

        BookmarkEntity.BookmarkEntityBuilder bookmarkEntity = BookmarkEntity.builder();

        if ( petFair != null ) {
            bookmarkEntity.user( petFair.getUser() );
        }

        return bookmarkEntity.build();
    }
}
