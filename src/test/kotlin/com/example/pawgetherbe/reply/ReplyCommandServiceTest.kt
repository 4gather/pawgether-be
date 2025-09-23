package com.example.pawgetherbe.reply

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyCreateRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.CommentEntity
import com.example.pawgetherbe.domain.entity.ReplyEntity
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.ReplyStatus
import com.example.pawgetherbe.exception.command.CommentCommandErrorCode
import com.example.pawgetherbe.exception.command.ReplyCommandErrorCode
import com.example.pawgetherbe.exception.command.UserCommandErrorCode
import com.example.pawgetherbe.mapper.command.ReplyCommandMapper
import com.example.pawgetherbe.repository.command.CommentCommandRepository
import com.example.pawgetherbe.repository.command.ReplyCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.ReplyCommandService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class ReplyCommandServiceTest: FreeSpec({
    lateinit var replyCommandService: ReplyCommandService
    lateinit var replyCommandRepository: ReplyCommandRepository
    lateinit var commentCommandRepository: CommentCommandRepository
    lateinit var userCommandRepository: UserCommandRepository
    lateinit var replyCommandMapper: ReplyCommandMapper

    lateinit var user: UserEntity
    lateinit var comment: CommentEntity
    lateinit var reply: ReplyEntity

    "답글 생성" - {
        "답글 생성 성공" {
            // given
            every { commentCommandRepository.findById(any()) } returns Optional.of(comment)

            val replyCreateRequest = ReplyCreateRequest(
                1L,
                "좋네요"
            )
            val replyCreateResponse = mockk<ReplyCommandDto.ReplyCreateResponse>()
            every { replyCommandMapper.toReplyCreateResponse(any()) } returns replyCreateResponse
            every { replyCommandRepository.save(any()) } returns reply

            // when
            val result = replyCommandService.replyCreate(replyCreateRequest)

            // then
            result shouldBe replyCreateResponse
            verify(exactly = 1){ replyCommandRepository.save(any()) }
        }
        "답글 생성 실패" - {
            val replyCreateRequest = ReplyCreateRequest(
                1L,
                "좋네요"
            )

            "user 정보가 없을때" {
                // given
                every { UserContext.getUserId() } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.replyCreate(replyCreateRequest)}

                // then
                exception.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
                verify(exactly = 0){ replyCommandRepository.save(any()) }
            }
            "comment 가 없을때" {
                // given
                every { commentCommandRepository.findById(replyCreateRequest.commentId) } throws CustomException(
                    CommentCommandErrorCode.NOT_FOUND_COMMENT
                )

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.replyCreate(replyCreateRequest)}

                // then
                exception.errorCode shouldBe CommentCommandErrorCode.NOT_FOUND_COMMENT
                verify(exactly = 0){ replyCommandRepository.save(any()) }
            }

            "저장중 에러발생" {
                // given
                every { commentCommandRepository.findById(any()) } returns Optional.of(comment)
                every { replyCommandRepository.save(any()) } throws CustomException(
                    ReplyCommandErrorCode.CREATE_INTERNAL_REPLY
                )

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.replyCreate(replyCreateRequest)}

                // then
                exception.errorCode shouldBe ReplyCommandErrorCode.CREATE_INTERNAL_REPLY
                verify(exactly = 1){ replyCommandRepository.save(any()) }
            }
        }
    }

    "답글 업데이트" - {
        "답글 업데이트 성공" {
            // given
            val replyUpdateRequest = ReplyCommandDto.ReplyUpdateRequest(
                1L, 1L, "변경 테스트"
            )
            var replyEntity = ReplyEntity.builder().content("테스트").build()
            var replyUpdateResponse = ReplyCommandDto.ReplyUpdateResponse(
                1L,
                1L,
                1L,
                replyUpdateRequest.content,
                "",
                "",
                0
            )
            every { replyCommandRepository.findById(any()) } returns Optional.of(replyEntity)
            every { replyCommandMapper.toReplyUpdateResponse(any()) } returns replyUpdateResponse

            // when
            var result = replyCommandService.updateReply(replyUpdateRequest)

            // then
            result shouldBe replyUpdateResponse
            result.content shouldBe replyUpdateRequest.content
        }
        "답글 업데이트 실패" - {
            val replyUpdateRequest = ReplyCommandDto.ReplyUpdateRequest(
                1L, 1L, "변경 테스트"
            )
            "user 정보 없을때" {
                // given
                every { UserContext.getUserId() } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.updateReply(replyUpdateRequest)}

                // then
                exception.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
            }
            "Comment 가 없을때" {
                // given
                every { commentCommandRepository.existsById(any()) } throws CustomException(CommentCommandErrorCode.NOT_FOUND_COMMENT)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.updateReply(replyUpdateRequest)}

                // then
                exception.errorCode shouldBe CommentCommandErrorCode.NOT_FOUND_COMMENT
            }
            "reply 이 없을때" {
                // given
                every { replyCommandRepository.findById(any()) } throws CustomException(ReplyCommandErrorCode.NOT_FOUND_REPLY)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.updateReply(replyUpdateRequest)}

                // then
                exception.errorCode shouldBe ReplyCommandErrorCode.NOT_FOUND_REPLY
            }
        }
    }

    "답글 삭제" - {
        "답글 삭제 성공" {
            // given
            var replyEntity = ReplyEntity.builder().status(ReplyStatus.ACTIVE).build()
            every { replyCommandRepository.findById(any()) } returns Optional.of(replyEntity)

            // when
            replyCommandService.deleteReply(1L, 1L)

            // then
            verify(exactly = 1) { replyCommandRepository.findById(1L) }
        }
        "답글 삭제 실패" - {
            "user 정보 없을때" {
                // given
                every { UserContext.getUserId() } throws CustomException(UserCommandErrorCode.NOT_FOUND_USER)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.deleteReply(1L, 1L)}

                // then
                exception.errorCode shouldBe UserCommandErrorCode.NOT_FOUND_USER
            }
            "Comment 가 없을때" {
                // given
                every { commentCommandRepository.existsById(any()) } throws CustomException(CommentCommandErrorCode.NOT_FOUND_COMMENT)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.deleteReply(1L, 1L)}

                // then
                exception.errorCode shouldBe CommentCommandErrorCode.NOT_FOUND_COMMENT
            }
            "답글이 이미 삭제되어있을경우" {
                // given
                var replyEntity = ReplyEntity.builder().status(ReplyStatus.REMOVED).build()
                every { replyCommandRepository.findById(any()) } returns Optional.of(replyEntity)

                // when
                val exception = shouldThrow<CustomException> {replyCommandService.deleteReply(1L, 1L)}

                // then
                exception.errorCode shouldBe ReplyCommandErrorCode.DELETE_CONFLICT_REPLY
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)

        user = mockk()
        reply = mockk()
        comment = mockk()

        replyCommandService = mockk(relaxed = true)
        replyCommandRepository = mockk(relaxed = true)
        commentCommandRepository = mockk(relaxed = true)
        userCommandRepository = mockk(relaxed = true)
        replyCommandMapper = mockk(relaxed = true)

        every { UserContext.getUserId() } returns "1"
        every { userCommandRepository.findById(1L) } returns Optional.of(user)

        every { userCommandRepository.existsById(any()) } returns true
        every { commentCommandRepository.existsById(any()) } returns true

        replyCommandService = ReplyCommandService(
            replyCommandRepository,
            commentCommandRepository,
            userCommandRepository,
            replyCommandMapper
        )
    }

    afterTest {
        unmockkStatic(UserContext::class)
    }
})