package com.example.pawgetherbe.service.checker;

import com.example.pawgetherbe.repository.command.ReplyCommandRepository;
import com.example.pawgetherbe.repository.query.ReplyQueryDSLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class ReplyTargetChecker implements TargetChecker {

    private final ReplyQueryDSLRepository replyQueryDSLRepository;

    @Override
    public boolean existsByTargetId(Long targetId) {
        return replyQueryDSLRepository.existsByReplyId(targetId);
    }

    @Override
    public boolean existsByTargetIdList(Set<Long> targetIdList) {
        Set<Long> existsReplyIdList = replyQueryDSLRepository.existsByReplyIdList(targetIdList);
        return existsReplyIdList.equals(targetIdList);
    }
}
