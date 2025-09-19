package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.ReplyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyCommandRepository extends JpaRepository<ReplyEntity, Long> {
}
