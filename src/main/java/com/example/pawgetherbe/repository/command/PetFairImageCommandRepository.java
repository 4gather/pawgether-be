package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.PetFairImageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetFairImageCommandRepository extends JpaRepository<PetFairImageEntity, Long> {
}
