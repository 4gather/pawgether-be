package com.example.pawgetherbe.service.checker;

import com.example.pawgetherbe.repository.query.CommentQueryDSLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CommentTargetChecker implements TargetChecker {

    private final CommentQueryDSLRepository commentQueryDSLRepository;

    @Override
    public boolean existsByTargetId(Long targetId) {
        return commentQueryDSLRepository.existsByCommentId(targetId);
    }

    @Override
    public boolean existsByTargetIdList(Set<Long> targetIdList) {
        Set<Long> existsCommentIdList = commentQueryDSLRepository.existsByCommentIdList(targetIdList);
        return existsCommentIdList.equals(targetIdList);
    }
}
