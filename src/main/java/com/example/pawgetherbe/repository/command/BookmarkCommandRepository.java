package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.BookmarkEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookmarkCommandRepository extends JpaRepository<BookmarkEntity, Long> {
    Optional<BookmarkEntity> findByPetFair_Id(long petFairId);
    void deleteByPetFair_Id(long petFairId);
}
