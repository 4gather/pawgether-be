package com.example.pawgetherbe.reply

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.ReplyQueryApi
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.query.CommentQueryErrorCode
import com.example.pawgetherbe.usecase.reply.ReadRepliesUseCase
import io.kotest.core.spec.style.FreeSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders

@ActiveProfiles("test")
class ReplyQueryApiTest: FreeSpec({
    lateinit var mockMvc: MockMvc
    lateinit var replyQueryApi: ReplyQueryApi
    lateinit var readRepliesUseCase: ReadRepliesUseCase

    "답글 조회" - {
        "답글 조회 성공" {
            // given
            var ReplyReadDtos = listOf(
                ReplyQueryDto.ReplyReadDto(1L, 1L, "test1", "test", "", "", 0),
                ReplyQueryDto.ReplyReadDto(2L, 1L, "test2", "test", "", "", 0),
                ReplyQueryDto.ReplyReadDto(3L, 1L, "test3", "test", "", "", 0)
            )
            var replyReadResponse = ReplyQueryDto.ReplyReadResponse(
                false,
                0,
                ReplyReadDtos
            )
            every { readRepliesUseCase.readReplies(any(), any()) } returns replyReadResponse

            // when
            mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/replies/{commentId}",1L)
            ).andExpect(status().isOk)
                .andExpect(jsonPath("$.hasMore").value(false))
                .andExpect(jsonPath("$.nextCursor").value(0))

            // then
            verify { readRepliesUseCase.readReplies(any(), any()) }
        }
        "답글 조회 실패" - {
            "comment 가 없을 때" {
                // given
                every { readRepliesUseCase.readReplies(any(), any()) } throws CustomException (CommentQueryErrorCode.NOT_FOUND_COMMENT_CALENDAR)

                // then,when
                mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/replies/{commentId}",1L)
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_COMMENT_CALENDAR"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("댓글이 존재하지 않습니다."))
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)
        every { UserContext.getUserId() } returns "1"

        replyQueryApi = mockk()
        readRepliesUseCase = mockk()

        replyQueryApi = ReplyQueryApi(
            readRepliesUseCase
        )

        mockMvc = MockMvcBuilders.standaloneSetup(replyQueryApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()
    }
    afterTest {
        unmockkStatic(UserContext::class)
    }
})