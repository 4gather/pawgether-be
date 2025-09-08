package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.PetFairEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetFairCommandRepository extends JpaRepository<PetFairEntity, Long> {
}
