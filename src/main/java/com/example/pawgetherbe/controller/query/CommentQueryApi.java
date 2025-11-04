package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.CommentQueryDto.ReadCommentResponse;
import com.example.pawgetherbe.usecase.comment.ReadCommentsUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentQueryApi {

    private final ReadCommentsUseCase readCommentsUseCase;

    @GetMapping("{petfairId}")
    public ReadCommentResponse readComments(@PathVariable("petfairId") long petfairId, @RequestParam(required = false, defaultValue = "0") long cursor) {
        return readCommentsUseCase.readComments(petfairId, cursor);
    }
}
