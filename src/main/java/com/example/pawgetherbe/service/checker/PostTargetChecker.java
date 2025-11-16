package com.example.pawgetherbe.service.checker;

import com.example.pawgetherbe.repository.query.PetFairQueryDSLRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class PostTargetChecker implements TargetChecker {

    private final PetFairQueryDSLRepository petFairQueryDSLRepository;

    @Override
    public boolean existsByTargetId(Long targetId) {
        return petFairQueryDSLRepository.existsActiveByPetFairId(targetId);
    }

    @Override
    public boolean existsByTargetIdList(Set<Long> targetIdList) {
        Set<Long> existsIdList = petFairQueryDSLRepository.existsActiveByPetFairIdList(targetIdList);
        return existsIdList.equals(targetIdList);
    }
}
