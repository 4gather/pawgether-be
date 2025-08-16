package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.controller.query.AccountQueryApi
import com.example.pawgetherbe.controller.query.dto.UserQueryDto
import com.example.pawgetherbe.usecase.jwt.query.RefreshQueryUseCase
import com.example.pawgetherbe.usecase.users.query.SignUpQueryUseCase
import com.fasterxml.jackson.databind.ObjectMapper
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
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [AccountQueryApi::class])
@ContextConfiguration(classes = [
    AccountQueryApi::class,
    AccountQueryApiTest.InternalMockConfig::class
])
@Import(GlobalExceptionHandler::class)
class AccountQueryApiTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @TestConfiguration
    class InternalMockConfig {
        @Bean fun refreshQueryUseCase(): RefreshQueryUseCase = mock()
        @Bean fun signUpQueryUseCase(): SignUpQueryUseCase = mock()
    }

    @Test
    fun `(2xx) 이메일 중복체크`() {
        val email = UserQueryDto.EmailCheckRequest("email@test.com")
        mockMvc.post("/api/v1/account/signup/email") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(email)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `(4xx) 이메일 중복 체크 실패 - 이메일 형식이 아님`() {
        val request = UserQueryDto.EmailCheckRequest("email")

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
            UserQueryDto.NickNameCheckRequest("nickname_123")
        mockMvc.post("/api/v1/account/signup/nickname") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(nickname)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `(4xx) 닉네임 중복 체크 실패 - 잘못된 형식`() {
        val request = UserQueryDto.NickNameCheckRequest("**")

        mockMvc.post("/api/v1/account/signup/nickname") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }
}