package com.example.pawgetherbe.repository.query;

import com.example.pawgetherbe.domain.entity.CommentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentQueryRepository extends JpaRepository<CommentEntity, Long> {
}
