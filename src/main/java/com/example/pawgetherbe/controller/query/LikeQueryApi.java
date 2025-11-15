package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.*;
import com.example.pawgetherbe.usecase.like.CountLikeUseCase;
import com.example.pawgetherbe.usecase.like.ExistsLikeUseCase;
import com.example.pawgetherbe.usecase.like.ReadLikesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeQueryApi {

    private final ReadLikesUseCase readLikesUseCase;
    private final ExistsLikeUseCase existsLikesUseCase;
    private final CountLikeUseCase countLikeUseCase;

    @GetMapping
    public List<SummaryLikesByUserResponse> readLikes() {
         return readLikesUseCase.readLikesByUser();
    }

    @GetMapping("/exists/{targetType}")
    public Set<ExistsLikeResponse> existLikeList(@PathVariable String targetType,
                                                  @RequestParam Set<Long> targetIds) {
        TargetRequests targetRequests = new TargetRequests(targetType, targetIds);
        return existsLikesUseCase.isExistLikeList(targetRequests);
    }

    @GetMapping("/exists/{targetType}/{targetId}")
    public ExistsLikeResponse existLikeDetail(@PathVariable("targetType") String targetType,
                                              @PathVariable("targetId") Long targetId) {
        TargetRequest targetRequest = new TargetRequest(targetType, targetId);
        return existsLikesUseCase.isExistLikeDetail(targetRequest);
    }

    @GetMapping("/counts/{targetType}")
    public Set<CountLikeResponse> countLikeList(@PathVariable("targetType") String targetType,
                                                @RequestParam Set<Long> targetIds) {
        TargetRequests targetRequests = new TargetRequests(targetType, targetIds);
        return countLikeUseCase.countLikeList(targetRequests);
    }

    @GetMapping("/counts/{targetType}/{targetId}")
    public CountLikeResponse countLikeDetail(@PathVariable("targetType") String targetType,
                                             @PathVariable("targetId") Long targetId) {
        TargetRequest targetRequest = new TargetRequest(targetType, targetId);
        return countLikeUseCase.countLikeDetail(targetRequest);
    }
}
