package com.example.pawgetherbe.controller.command;

import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeRequest;
import com.example.pawgetherbe.controller.command.dto.LikeCommandDto.LikeResponse;
import com.example.pawgetherbe.usecase.like.CancelLikeUseCase;
import com.example.pawgetherbe.usecase.like.LikeUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/like")
@RequiredArgsConstructor
public class LikeCommandApi {

    private final LikeUseCase likeUseCase;
    private final CancelLikeUseCase cancelLikeUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LikeResponse addLike(@Valid @RequestBody LikeRequest request) {
        return likeUseCase.addLike(request);
    }

    @DeleteMapping
    public LikeResponse cancelLike(@Valid @RequestBody LikeRequest request) {
        return cancelLikeUseCase.cancelLike(request);
    }
}
