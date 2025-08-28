package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.entity.PetFairEntity;
import com.example.pawgetherbe.domain.status.PostStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PetFairQueryRepository extends JpaRepository<PetFairEntity, Long> {
    Optional<PetFairEntity> findByIdAndPostStatus(Long petFairId, PostStatus status);
}
