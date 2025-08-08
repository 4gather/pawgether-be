package com.example.pawgetherbe.account

import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.AccountApi
import com.example.pawgetherbe.controller.dto.UserDto
import com.example.pawgetherbe.usecase.users.DeleteUserUseCase
import com.example.pawgetherbe.usecase.users.EditUserUseCase
import com.example.pawgetherbe.usecase.users.SignOutUseCase
import com.example.pawgetherbe.usecase.users.SignUpWithIdUseCase
import com.example.pawgetherbe.usecase.users.SignUpWithOauthUseCase
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.web.server.ResponseStatusException

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [AccountApi::class])
@ContextConfiguration(classes = [
    AccountApi::class,
    AccountApiTest.InternalMockConfig::class
])
class AccountApiTest {

    @Autowired
    private lateinit var editUserUseCase: EditUserUseCase

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class InternalMockConfig {
        @Bean fun signUpWithIdUseCase(): SignUpWithIdUseCase = mock()
        @Bean fun signUpWithOauthUseCase(): SignUpWithOauthUseCase = mock()
        @Bean fun deleteUserUseCase(): DeleteUserUseCase = mock()
        @Bean fun signOutUseCase(): SignOutUseCase = mock()
        @Bean fun editUserUseCase(): EditUserUseCase = mock()
        @Bean fun oauthConfig(): OauthConfig = mock()
    }


    @Test
    fun `(2xx) 회원가입 성공`() {
        val request = UserDto.UserSignUpRequest("test", "email@test.com", "password123*",)
        mockMvc.post("/api/v1/account/signup") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `(4xx) 회원가입 실패 - 이메일 형식이 잘못됨`() {
        val request = UserDto.UserSignUpRequest(
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
    fun `(2xx) 이메일 중복체크`() {
        val email = UserDto.EmailCheckRequest("email@test.com")
        mockMvc.post("/api/v1/account/signup/email") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(email)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `(4xx) 이메일 중복 체크 실패 - 이메일 형식이 아님`() {
        val request = UserDto.EmailCheckRequest("email")

        mockMvc.post("/api/v1/account/signup/email") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `(2xx) 닉네임 중복체크`() {
        val nickname =
            UserDto.NickNameCheckRequest("nickname_123")
        mockMvc.post("/api/v1/account/signup/nickname") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(nickname)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `(4xx) 닉네임 중복 체크 실패 - 잘못된 형식`() {
        val request = UserDto.NickNameCheckRequest("**")

        mockMvc.post("/api/v1/account/signup/nickname") {
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
        val request = UserDto.UpdateUserRequest("newNick", "img.jpg")

        whenever(editUserUseCase.updateUserInfo(any(), any())).thenReturn(
            UserDto.UpdateUserResponse(
                "access123",
                "refresh123",
                "img.jpg",
                "newNick"
            )
        )

        mockMvc.patch("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(Cookie("refresh_token", "refresh123"))
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value("access123") }
            jsonPath("$.userImg") { value("img.jpg") }
            jsonPath("$.nickName") { value("newNick") }
        }
    }

    @Test
    fun `(4xx) 유저 정보 수정 실패 - 파라미터 없음`() {
        val request = UserDto.UpdateUserRequest("newNick", "img.jpg")

        whenever(editUserUseCase.updateUserInfo(any(), any()))
            .thenThrow(
                ResponseStatusException(HttpStatus.NOT_FOUND, " 존재하지 않는 계정입니다.")
            )

        mockMvc.patch("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            cookie(Cookie("refresh_token", "refresh123"))
        }.andExpect {
            status { isNotFound() }
        }
    }
}