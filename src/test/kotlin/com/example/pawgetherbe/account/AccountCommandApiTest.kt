package com.example.pawgetherbe.account

import com.example.pawgetherbe.common.exceptionHandler.CustomException
import com.example.pawgetherbe.common.exceptionHandler.GlobalExceptionHandler
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.command.AccountCommandApi
import com.example.pawgetherbe.controller.command.dto.UserCommandDto
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserRequest
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.SignInUserWithRefreshTokenResponse
import com.example.pawgetherbe.exception.command.UserCommandErrorCode.NOT_FOUND_USER
import com.example.pawgetherbe.exception.command.UserCommandErrorCode.UNAUTHORIZED_LOGIN
import com.example.pawgetherbe.mapper.command.UserCommandMapper
import com.example.pawgetherbe.service.command.UserCommandService.REFRESH_TOKEN_VALIDITY_SECONDS
import com.example.pawgetherbe.usecase.jwt.command.RefreshCommandUseCase
import com.example.pawgetherbe.usecase.users.command.*
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
import org.springframework.test.web.servlet.*
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
    private lateinit var signInUseCase: SignInCommandUseCase

    @Autowired
    private lateinit var refreshUseCase: RefreshCommandUseCase

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

    @Test
    fun `(2xx) 로그인`() {
        val normalRequest = UserCommandDto.SignInUserRequest("test@test.com", "test123!@#")
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"

        whenever(signInUseCase.signIn(any()))
            .thenReturn(
                SignInUserWithRefreshTokenResponse(
                    accessToken,
                    refreshToken,
                    "Google",
                    "test@test.com",
                    "nickName",
                    "img.jpg"
                )
            )

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(normalRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.accessToken") { value(accessToken) }
            jsonPath("$.provider") { value("Google") }
            jsonPath("$.email") { value(normalRequest.email) }
            jsonPath("$.nickname") { value("nickName") }
            jsonPath("$.userImg") { value("img.jpg") }
            cookie {
                exists(refreshToken)
                value(refreshToken, "refreshToken")
                httpOnly(refreshToken, true)
                secure(refreshToken, true)
                path(refreshToken, "/")
                maxAge(refreshToken, REFRESH_TOKEN_VALIDITY_SECONDS)
                sameSite(refreshToken, "Strict")
            }
        }
    }

    @Test
    fun `(4xx) 없는 계정으로 로그인`() {
        val noAccountRequest = SignInUserRequest("noAccount@test.com", "test123!@#")

        whenever(signInUseCase.signIn(any()))
            .thenThrow(
                CustomException(NOT_FOUND_USER)
            )

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(noAccountRequest)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.httpStatus") { value(HttpStatus.NOT_FOUND) }
            jsonPath("$.code") { value(NOT_FOUND_USER) }
            jsonPath("$.message") { value("존재하지 않는 계정입니다.") }
        }
    }

    @Test
    fun `(4xx) 이메일 입력 없음`() {
        val emailNullRequest = SignInUserRequest("", "test123!@#")

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(emailNullRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error[0].defaultMessage") { value("이메일을 입력해 주세요")}
        }
    }

    @Test
    fun `(4xx) 이메일 입력 공백`() {
        val emailBlankRequest = SignInUserRequest(" ", "test123!@#")

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(emailBlankRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error[0].defaultMessage") { value("이메일을 입력해 주세요")}
        }
    }

    @Test
    fun `(4xx) 유효하지 않은 이메일 형식 입력`() {
        val invalidEmailFormatRequest = SignInUserRequest("test", "test123!@#")

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(invalidEmailFormatRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error[0].defaultMessage") { value("이메일 형식을 지켜주세요")}
        }
    }

    @Test
    fun `(4xx) 비밀번호 값을 공백으로 입력`() {
        val blankPasswordRequest = SignInUserRequest("test@test.com", "")

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(blankPasswordRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error[0].defaultMessage") { value("비밀번호를 입력해주세요")}
        }
    }

    @Test
    fun `(4xx) 비밀번호 패턴 미준수`() {
        val blankPasswordRequest = SignInUserRequest("test@test.com", "")

        mockMvc.post("/api/v1/account") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(blankPasswordRequest)
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.error[0].defaultMessage") { value("비밀번호는 영문, 숫자, 특수문자를 포함해 8~20자로 입력해주세요")}
        }
    }
    // TODO: 중복 로그인 처리?

    @Test
    fun `(2xx) 만료된 토큰 갱신`() {
        val authHeader = "Bearer accessToken"
        val accessToken = "accessToken"
        val refreshToken = "refreshToken"
        val renewAccessToken = "renewAccessToken"
        val renewRefreshToken = "renewRefreshToken"

        whenever(refreshUseCase.refresh(any(), any()))
            .thenReturn(
                mapOf(
                    "accessToken" to renewAccessToken,
                    "refreshToken" to renewRefreshToken
                )
            )

        mockMvc.post("/api/v1/account/refresh") {
            header("Authorization", authHeader)
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isCreated() }
            content { string(renewRefreshToken)}
            cookie {
                exists(renewRefreshToken)
                value(refreshToken, "renewRefreshToken")
                httpOnly(refreshToken, true)
                secure(refreshToken, true)
                path(refreshToken, "/")
                maxAge(refreshToken, REFRESH_TOKEN_VALIDITY_SECONDS)
                sameSite(refreshToken, "Strict")
            }
        }
    }
    @Test
    fun `(4xx) Authorization 헤더가 Bearer 로 시작하지 않음`() {
        val authHeader = "inValidAuthHeader"
        val refreshToken = "refreshToken"

        whenever(refreshUseCase.refresh(any(), any()))
            .thenThrow(
                CustomException(UNAUTHORIZED_LOGIN)
            )

        mockMvc.post("/api/v1/account/refresh") {
            header("Authorization", authHeader)
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.httpStatus") { value(HttpStatus.UNAUTHORIZED) }
            jsonPath("$.code") { value(UNAUTHORIZED_LOGIN) }
            jsonPath("$.message") { value("다시 로그인을 진행해주세요.") }
        }
    }

    @Test
    fun `(4xx) 유효한 accessToken 입력`() {
        // TODO: Service 로직에서 CustomException 처리 필요할 것 같음
    }

    @Test
    fun `(4xx) refresh token 만료`() {
        val authHeader = "Bearer accessToken"
        val refreshToken = "refreshToken"

        whenever(refreshUseCase.refresh(any(), any()))
            .thenThrow(
                CustomException(UNAUTHORIZED_LOGIN)
            )

        mockMvc.post("/api/v1/account/refresh") {
            header("Authorization", authHeader)
            cookie(Cookie("refreshToken", refreshToken))
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.httpStatus") { value(HttpStatus.UNAUTHORIZED) }
            jsonPath("$.code") { value(UNAUTHORIZED_LOGIN) }
            jsonPath("$.message") { value("다시 로그인을 진행해주세요.") }
        }
    }

}