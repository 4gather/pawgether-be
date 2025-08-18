package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.command.AccountCommandApi
import com.example.pawgetherbe.controller.command.dto.UserCommandDto
import com.example.pawgetherbe.mapper.command.UserCommandMapper
import com.example.pawgetherbe.usecase.jwt.command.RefreshCommandUseCase
import com.example.pawgetherbe.usecase.users.command.DeleteUserCommandUseCase
import com.example.pawgetherbe.usecase.users.command.EditUserCommandUseCase
import com.example.pawgetherbe.usecase.users.command.SignInCommandUseCase
import com.example.pawgetherbe.usecase.users.command.SignOutCommandUseCase
import com.example.pawgetherbe.usecase.users.command.SignUpCommandOauthUseCase
import com.example.pawgetherbe.usecase.users.command.SignUpCommandUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.web.server.ResponseStatusException

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [AccountCommandApi::class])
@ContextConfiguration(classes = [
    AccountCommandApi::class,
    AccountCommandApiTest.InternalMockConfig::class
])
@Import(GlobalExceptionHandler::class)
class AccountCommandApiTest {

    @Autowired
    private lateinit var editUserUseCase: EditUserCommandUseCase

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class InternalMockConfig {
        @Bean fun refreshCommandUseCase(): RefreshCommandUseCase = mock()
        @Bean fun signUpWithIdUseCase(): SignUpCommandUseCase = mock()
        @Bean fun signUpWithOauthUseCase(): SignUpCommandOauthUseCase = mock()
        @Bean fun deleteUserUseCase(): DeleteUserCommandUseCase = mock()
        @Bean fun signOutUseCase(): SignOutCommandUseCase = mock()
        @Bean fun editUserUseCase(): EditUserCommandUseCase = mock()
        @Bean fun signInUseCase(): SignInCommandUseCase = mock()
        @Bean fun userMapper(): UserCommandMapper = mock()
        @Bean fun oauthConfig(): OauthConfig = mock()
    }


    @Test
    fun `(2xx) 회원가입 성공`() {
        val request = UserCommandDto.UserSignUpRequest("test", "email@test.com", "password123*")
        mockMvc.post("/api/v1/account/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `(4xx) 회원가입 실패 - 이메일 형식이 잘못됨`() {
        val request = UserCommandDto.UserSignUpRequest(
            "nickname123",
           "email",
            "password123*"
        )

        mockMvc.post("/api/v1/account/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `(2xx) 로그아웃`() {
        mockMvc.get("/api/v1/account") {
            cookie(Cookie("refresh_token", "sample-refresh-token"))
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `(2xx) 계정 삭제`() {
        mockMvc.delete("/api/v1/account") {
            cookie(Cookie("refresh_token", "sample-refresh-token"))
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun `(2xx) 유저 정보 수정`() {
        val request = UserCommandDto.UpdateUserRequest("newNick", "img.jpg")

        whenever(editUserUseCase.updateUserInfo(any())).thenReturn(
            UserCommandDto.UpdateUserResponse(
                "newNick",
                "img.jpg"
            )
        )

        mockMvc.patch("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.userImg") { value("img.jpg") }
            jsonPath("$.nickName") { value("newNick") }
        }
    }

    @Test
    fun `(4xx) 유저 정보 수정 실패 - 파라미터 없음`() {
        val request = UserCommandDto.UpdateUserRequest("newNick", "img.jpg")

        whenever(editUserUseCase.updateUserInfo(any()))
            .thenThrow(
                ResponseStatusException(HttpStatus.NOT_FOUND, " 존재하지 않는 계정입니다.")
            )

        mockMvc.patch("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isNotFound() }
        }
    }
}