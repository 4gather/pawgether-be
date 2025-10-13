package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.LikeQueryDto.SummaryLikesByUserResponse;
import com.example.pawgetherbe.usecase.like.ReadLikesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/likes")
public class LikeQueryApi {

    private final ReadLikesUseCase readLikesUseCase;

    @GetMapping
    public List<SummaryLikesByUserResponse> readLikes() {
         return readLikesUseCase.readLikesByUser();
    }
}
