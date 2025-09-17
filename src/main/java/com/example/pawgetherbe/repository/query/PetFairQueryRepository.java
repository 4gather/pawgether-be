package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.entity.PetFairEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetFairQueryRepository extends JpaRepository<PetFairEntity, Long> {
}
