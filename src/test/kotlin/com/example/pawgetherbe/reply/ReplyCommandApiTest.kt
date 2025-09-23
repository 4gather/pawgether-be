package com.example.pawgetherbe.reply

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.command.ReplyCommandApi
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto
import com.example.pawgetherbe.controller.command.dto.ReplyCommandDto.ReplyUpdateRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.exception.command.CommentCommandErrorCode
import com.example.pawgetherbe.exception.command.ReplyCommandErrorCode
import com.example.pawgetherbe.exception.command.UserCommandErrorCode
import com.example.pawgetherbe.usecase.reply.DeleteReplyUseCase
import com.example.pawgetherbe.usecase.reply.EditReplyUseCase
import com.example.pawgetherbe.usecase.reply.RegistryReplyUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.kotest.core.spec.style.FreeSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
@ActiveProfiles("test")
class ReplyCommandApiTest: FreeSpec({
    lateinit var mockMvc: MockMvc
    lateinit var replyCommandApi: ReplyCommandApi
    lateinit var registryReplyUseCase: RegistryReplyUseCase
    lateinit var editReplyUseCase: EditReplyUseCase
    lateinit var deleteReplyUseCase: DeleteReplyUseCase
    lateinit var objectMapper: ObjectMapper

    "답글 생성 api" - {
        "답글 생성 성공" {
            // given
            var replyCreateRequest = ReplyCommandDto.ReplyCreateRequest(
                1L,
                "test"
            )
            var replyCreateResponse = ReplyCommandDto.ReplyCreateResponse(
                1L,
                1L,
                1L,
                "test",
                "",
                "",
                0
            )

            every { registryReplyUseCase.replyCreate(any()) } returns replyCreateResponse

            // when
            mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/replies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(replyCreateRequest))
            ).andExpect(status().isOk)

            // then
            verify(exactly = 1) { registryReplyUseCase.replyCreate(any()) }
        }

        "답글 생성 실패" - {
            var replyCreateRequest = ReplyCommandDto.ReplyCreateRequest(
                1L,
                "test"
            )

            "user 정보가 없을 때" {
                // given
                every { registryReplyUseCase.replyCreate(any()) } throws CustomException (UserCommandErrorCode.NOT_FOUND_USER)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/replies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(replyCreateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "comment 가 없을 때" {
                // given
                every { registryReplyUseCase.replyCreate(any()) } throws CustomException (CommentCommandErrorCode.NOT_FOUND_COMMENT)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/replies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(replyCreateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_COMMENT"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("댓글이 존재하지 않습니다."))
            }
            "생성 실패했을때" {
                // given
                every { registryReplyUseCase.replyCreate(any()) } throws CustomException (ReplyCommandErrorCode.CREATE_INTERNAL_REPLY)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/replies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(replyCreateRequest))
                ).andExpect(status().is5xxServerError)
                    .andExpect(jsonPath("$.code").value("CREATE_INTERNAL_REPLY"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                    .andExpect(jsonPath("$.message").value("답글 생성중 오류가 발생했습니다."))
            }
        }
    }

    "답글 업데이트" - {
        "답글 업데이트 성공" {
            // given
            var replyUpdateRequest = ReplyUpdateRequest(
                1L,
                1L,
                "test"
            )
            var replyUpdateResponse = ReplyCommandDto.ReplyUpdateResponse(
                1L,
                1L,
                1L,
                "test",
                "",
                "",
                0
            )
            every { editReplyUseCase.updateReply(any()) } returns replyUpdateResponse

            // when
            mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/replies")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .content(objectMapper.writeValueAsString(replyUpdateRequest))
            ).andExpect(status().isOk)

            //then
            verify(exactly = 1) { editReplyUseCase.updateReply(any()) }
        }

        "답글 업데이트 실패" - {
            var replyUpdateRequest = ReplyUpdateRequest(
                1L,
                1L,
                "test"
            )
            "user 정보가 없을 때" {
                // given
                every { editReplyUseCase.updateReply(any()) } throws CustomException (UserCommandErrorCode.NOT_FOUND_USER)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/replies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(replyUpdateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "comment 가 없을 때" {
                // given
                every { editReplyUseCase.updateReply(any()) } throws CustomException (CommentCommandErrorCode.NOT_FOUND_COMMENT)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/replies")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(objectMapper.writeValueAsString(replyUpdateRequest))
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_COMMENT"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("댓글이 존재하지 않습니다."))
            }
        }
    }

    "답글 삭제" - {
        "답글 삭제 성공" {
            // given
            every { deleteReplyUseCase.deleteReply(any(), any()) } just Runs

            // when
            mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/replies/{commentId}/{replyId}",1L,1L)
            ).andExpect(status().isNoContent)

            //then
            verify(exactly = 1) { deleteReplyUseCase.deleteReply(any(), any()) }
        }
        "답글 삭제 실패" - {
            "user 정보가 없을 때" {
                // given
                every { deleteReplyUseCase.deleteReply(any(), any()) } throws CustomException (UserCommandErrorCode.NOT_FOUND_USER)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/replies/{commentId}/{replyId}",1L,1L)
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_USER"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("존재하지 않는 계정입니다."))
            }
            "comment 가 없을 때" {
                // given
                every { deleteReplyUseCase.deleteReply(any(), any()) } throws CustomException (CommentCommandErrorCode.NOT_FOUND_COMMENT)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/replies/{commentId}/{replyId}",1L,1L)
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("NOT_FOUND_COMMENT"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.NOT_FOUND.value()))
                    .andExpect(jsonPath("$.message").value("댓글이 존재하지 않습니다."))
            }
            "이미 삭제된 답글일때" {
                // given
                every { deleteReplyUseCase.deleteReply(any(), any()) } throws CustomException (ReplyCommandErrorCode.DELETE_CONFLICT_REPLY)

                // then,when
                mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/replies/{commentId}/{replyId}",1L,1L)
                ).andExpect(status().is4xxClientError)
                    .andExpect(jsonPath("$.code").value("DELETE_CONFLICT_REPLY"))
                    .andExpect(jsonPath("$.status").value(HttpStatus.CONFLICT.value()))
                    .andExpect(jsonPath("$.message").value("이미 삭제된 답글입니다."))
            }
        }
    }

    beforeTest {
        mockkStatic(UserContext::class)
        every { UserContext.getUserId() } returns "1"

        registryReplyUseCase = mockk()
        editReplyUseCase = mockk()
        deleteReplyUseCase = mockk()

        replyCommandApi = ReplyCommandApi(
            registryReplyUseCase,
            editReplyUseCase,
            deleteReplyUseCase
        )

        objectMapper = ObjectMapper()
            .registerModule(JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)

        val converter = MappingJackson2HttpMessageConverter(objectMapper)
        mockMvc = MockMvcBuilders.standaloneSetup(replyCommandApi)
            .setControllerAdvice(GlobalExceptionHandler())
            .setMessageConverters(converter)
            .build()
    }
    afterTest {
        unmockkStatic(UserContext::class)
    }
})