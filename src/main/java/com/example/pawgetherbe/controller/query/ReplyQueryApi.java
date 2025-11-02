package com.example.pawgetherbe.controller.query;

import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyCountResponse;
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse;
import com.example.pawgetherbe.usecase.reply.ReadRepliesUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/replies")
@RequiredArgsConstructor
public class ReplyQueryApi {

    private final ReadRepliesUseCase readRepliesUseCase;

    @GetMapping("/{commentId}")
    public ReplyReadResponse readReplies(@PathVariable long commentId, @RequestParam(required = false, defaultValue = "0") long cursor) {
        return readRepliesUseCase.readReplies(commentId, cursor);
    }

    @PostMapping("/count")
    public ReplyCountResponse replyCountResponse(@RequestBody List<Long> commentIdList) {
        return readRepliesUseCase.replyCountResponse(commentIdList);
    }
}
