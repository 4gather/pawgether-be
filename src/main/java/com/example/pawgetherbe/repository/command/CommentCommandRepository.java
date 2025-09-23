package com.example.pawgetherbe.repository.command;

import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentCommandRepository extends JpaRepository<CommentEntity, Long> {
}
