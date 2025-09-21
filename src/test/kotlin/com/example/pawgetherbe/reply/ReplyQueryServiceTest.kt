package com.example.pawgetherbe.reply

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto
import com.example.pawgetherbe.controller.query.dto.ReplyQueryDto.ReplyReadResponse
import com.example.pawgetherbe.domain.entity.CommentEntity
import com.example.pawgetherbe.domain.entity.ReplyEntity
import com.example.pawgetherbe.exception.query.CommentQueryErrorCode
import com.example.pawgetherbe.repository.query.CommentQueryRepository
import com.example.pawgetherbe.repository.query.ReplyQueryDSLRepository
import com.example.pawgetherbe.service.query.ReplyQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class ReplyQueryServiceTest: FreeSpec({
    lateinit var replyQueryService: ReplyQueryService
    lateinit var replyQueryDSLRepository: ReplyQueryDSLRepository
    lateinit var commentQueryRepository: CommentQueryRepository

    lateinit var comment: CommentEntity
    lateinit var reply: ReplyEntity

    "답글 조회" - {
        "답글 조회 성공" {
            // given
            var ReplyReadDtos = listOf(
                ReplyQueryDto.ReplyReadDto(1L,1L, "a", "a", "", "", 0),
                ReplyQueryDto.ReplyReadDto(2L,1L, "b", "b", "", "", 0),
                ReplyQueryDto.ReplyReadDto(3L,1L, "c", "c", "", "", 0)
            )
            var ReplyReadResponse = ReplyReadResponse(
                false,
                0,
                ReplyReadDtos
            )

            every { commentQueryRepository.existsById(any()) } returns true
            every { replyQueryDSLRepository.readReplies(any(), any()) } returns ReplyReadResponse

            // when
            var result = replyQueryService.readReplies(1L, 0)

            // then
            result shouldBe ReplyReadResponse
            verify(exactly = 1) { replyQueryDSLRepository.readReplies(any(), any()) }
        }
        "답글 조회 실패" - {
            "comment 존재하지 않을때" {
                // given
                every { commentQueryRepository.existsById(any()) } returns false

                // when
                val exception = shouldThrow<CustomException> {replyQueryService.readReplies(1L, 0)}

                // then
                exception.errorCode shouldBe CommentQueryErrorCode.NOT_FOUND_COMMENT_CALENDAR
            }
        }
    }

    beforeTest {
        comment = mockk()
        replyQueryService = mockk()
        commentQueryRepository = mockk()
        replyQueryDSLRepository = mockk()

        replyQueryService = ReplyQueryService(replyQueryDSLRepository, commentQueryRepository)
    }
})