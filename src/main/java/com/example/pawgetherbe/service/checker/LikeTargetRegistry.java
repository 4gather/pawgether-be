package com.example.pawgetherbe.service.checker;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class LikeTargetRegistry {
    private final Map<String, TargetChecker> checkers;

    public boolean existsByOneTarget(String targetType, Long targetId) {
        String targetBeanName = String.format("%sTargetChecker", targetType.toLowerCase());
        TargetChecker checker = checkers.get(targetBeanName);
        if (checker == null) {
            return false;
        }

        return checker.existsByTargetId(targetId);
    }

    public boolean existsByTargetList(String targetType, Set<Long> targetIdList) {
        String targetBeanName = String.format("%sTargetChecker", targetType.toLowerCase());
        TargetChecker checker = checkers.get(targetBeanName);
        if (checker == null) {
            return false;
        }

        return checker.existsByTargetIdList(targetIdList);
    }
}
