package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.LikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeCommandRepository extends JpaRepository<LikeEntity, Long> {
}
