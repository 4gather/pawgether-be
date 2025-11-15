package com.example.pawgetherbe.service.checker;

import java.util.Set;

public interface TargetChecker {
    boolean existsByTargetId(Long targetId);
    boolean existsByTargetIdList(Set<Long> targetIds);
}
