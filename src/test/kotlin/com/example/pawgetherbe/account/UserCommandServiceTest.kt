package com.example.pawgetherbe.account
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.command.dto.UserCommandDto
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.domain.status.UserStatus
import com.example.pawgetherbe.mapper.command.UserCommandMapper
import com.example.pawgetherbe.repository.command.OauthCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.service.command.UserCommandService
import com.example.pawgetherbe.util.EncryptUtil
import com.example.pawgetherbe.util.EncryptUtil.passwordEncode
import com.example.pawgetherbe.util.JwtUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.verify
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class UserCommandServiceTest: FreeSpec({
    lateinit var userCommandRepository: UserCommandRepository
    lateinit var oauthCommandRepository: OauthCommandRepository
    lateinit var userCommandMapper: UserCommandMapper
    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var jwtUtil: JwtUtil
    lateinit var oauthConfig: OauthConfig
    lateinit var userCommandService: UserCommandService

    "회원가입" - {
        "정상적으로 가입" {
            // Given
            val signUpRequest = UserSignUpRequest(
                "tester",
                "test@example.com",
                "password123!"
            )
            val entity = UserEntity.builder()
                .nickName("tester")
                .email("test@example.com")
                .password("password123")
                .build()

            val savedEntity = entity.toBuilder()
                .password(passwordEncode(entity.password))
                .status(UserStatus.ACTIVE)
                .role(UserRole.USER_EMAIL)
                .build()

            every { userCommandMapper.toUserEntity(signUpRequest) } returns entity
            every { userCommandRepository.save(any()) } returns savedEntity

            // When
            userCommandService.signUp(signUpRequest)

            // Then
            val slot = slot<UserEntity>()
            verify { userCommandRepository.save(capture(slot)) }

            val savedUser = slot.captured
            savedUser.email shouldBe "test@example.com"
            savedUser.nickName shouldBe "tester"
            savedUser.status shouldBe UserStatus.ACTIVE
            savedUser.role shouldBe UserRole.USER_EMAIL
        }

        "이메일 중복으로 가입 실패" {
            // Given
            val signUpRequest = UserSignUpRequest(
                "tester",
                "test@example.com",
                "password123!"
            )
            val entity = UserEntity.builder()
                .nickName("tester")
                .email("test@example.com")
                .password("password123")
                .build()

            every { userCommandMapper.toUserEntity(signUpRequest) } returns entity
            every { userCommandRepository.existsByEmail("test@example.com") } returns true

            // When & Then
            val exception = shouldThrow<RuntimeException> {
                userCommandService.signUp(signUpRequest)
            }

            verify(exactly = 0) { userCommandRepository.save(any()) }
        }
    }

    "회원 탈퇴" - {
        "이메일 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { UserContext.getUserId() } returns "1"
            every { oauthCommandRepository.existsByUser_Id(id) } returns false
            every { userCommandRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.deleteAccount(refreshToken)

            verify { userCommandRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }

        "간편 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { oauthCommandRepository.existsByUser_Id(id) } returns true
            every { oauthCommandRepository.deleteByUser_Id(id) } just Runs
            every { userCommandRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.deleteAccount(refreshToken)

            verify { oauthCommandRepository.deleteByUser_Id(1L) }
            verify { userCommandRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "로그아웃" - {
        "refreshToken 삭제 성공" {
            val refreshToken = "refreshToken123"

            every { redisTemplate.delete(refreshToken) } returns true

            userCommandService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
        "refreshToken 삭제 실패" {
            val refreshToken = "refreshToken123"
            every { redisTemplate.delete(refreshToken) } returns false

            userCommandService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "회원 정보 수정" - {
        "nickname과 userImg가 업데이트" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"

            val newNickname = "newNick"
            val newUserImg = "newImgUrl"
            val id = UserContext.getUserId().toLong()
            val user = UserEntity.builder()
                .id(1L)
                .role(UserRole.USER_EMAIL)
                .status(UserStatus.ACTIVE)
                .nickName("tester")
                .userImg("/img/2025/05/05/202505.webp")
                .email("test@example.com")
                .password(passwordEncode("password123"))
                .build()

            val request = UserCommandDto.UpdateUserRequest(newNickname, newUserImg)
            val expectedResponse = UserCommandDto.UpdateUserResponse(newNickname, newUserImg)

            every { userCommandRepository.findById(id) } returns Optional.of(user)
            every { userCommandMapper.toUpdateUserResponse(request) } returns expectedResponse

            val result = userCommandService.updateUserInfo(request)
            verify {
                userCommandRepository.findById(id)
                user.updateProfile(newNickname, newUserImg)
                userCommandMapper.toUpdateUserResponse(request)
            }
            println("Result: $result")
            println("Expected nickName: $newNickname, Actual nickName: ${result.nickName}")
            println("Expected userImg: $newUserImg, Actual userImg: ${result.userImg}")
            result.nickName shouldBe newNickname
            result.userImg shouldBe newUserImg
        }
        "업데이트 실패" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"
            val id = UserContext.getUserId().toLong()
            val newNickname = "newNick"
            val newUserImg = "newImgUrl"

            every { userCommandRepository.findById(id) } returns Optional.empty()

            val exception = shouldThrow<RuntimeException> {
                userCommandService.updateUserInfo(
                    UserCommandDto.UpdateUserRequest(newNickname, newUserImg),
                )
            }

            exception.message shouldBe "존재하지 않는 계정입니다."
        }
    }

    "기능] 로그인" - {
        // Given
        val encryptPassword = EncryptUtil.passwordEncode("testUser123!@#")
        val savedRequest = SignInUserResponse("accessToken",
            "Google",
            "testUser1@test.com",
            "tester",
            "/img/2025/05/05/202505.webp")

        "2XX] 로그인 성공" - {
            // Given
            val signInRequest = SignInUserRequest("testUser1@test.com", "testUser123!@#")
            val userEntity = UserEntity.builder()
                .id(1L)
                .email("testUser1@test.com")
                .password(encryptPassword)
                .nickName("tester")
                .userImg("/img/2025/05/05/202505.webp")
                .build()
            val signInWithRefreshToken = SignInUserWithRefreshTokenResponse(
                "accessToken", "refreshToken", "Google", "testUser1@test.com", "tester", "/img/2025/05/05/202505.webp")
            val userAccessTokenDto = UserAccessTokenDto(1L, UserRole.USER_EMAIL)

            every { userRepository.findByEmail(signInRequest.email) } returns userEntity
            every { redisTemplate.opsForValue().set(any<String>(), any<String>(), any<Duration>()) } just Runs
            every { jwtUtil.generateAccessToken(userAccessTokenDto) } returns "accessToken"
            every { userMapper.toSignInWithRefreshToken(userEntity, any(), any()) } returns signInWithRefreshToken

            // When
            val result = userService.signIn(signInRequest);

            "회원 존재" {
                result shouldNotBe null
                result.email shouldBe signInRequest.email
            }
            "accessToken 정상 발급" {
                result.accessToken shouldBe "accessToken"
            }
            "refreshToken 정상 발급" {
                result.refreshToken shouldBe "refreshToken"
            }
//            "valkey를 이용한 refresh token 저장" {
//                verify(exactly = 1) { redisTemplate.opsForValue().set("refreshToken", userEntity.id.toStr(), Duration.ofDays(7))}
//            }
        }

        "4XX] 로그인 실패" - {
            "입력한 email 계정 없음" {
                // Given
                val signInRequest = SignInUserRequest("inValidUser1@test.com", "testUser123!@#")

                every { userRepository.findByEmail(signInRequest.email) } returns null

                // When
                val exception = shouldThrow<ResponseStatusException> {
                    userService.signIn(signInRequest)
                }

                // Then
                verify(exactly = 1) { userRepository.findByEmail(signInRequest.email) }

                exception.statusCode shouldBeEqual HttpStatus.NOT_FOUND
                exception.reason shouldBeEqual "존재하지 않는 계정입니다."
            }
            "틀린 패스워드를 입력한 경우" {
                // Given
                val signInRequest = SignInUserRequest("testUser1@test.com", "invalidPassword123!@#")
                val userEntity = UserEntity.builder()
                    .id(1L)
                    .email("testUser1@test.com")
                    .password(encryptPassword)
                    .nickName("tester")
                    .userImg("/img/2025/05/05/202505.webp")
                    .build()

                every { userRepository.findByEmail(signInRequest.email) } returns userEntity

                // When
                val exception = shouldThrow<ResponseStatusException> {
                    userService.signIn(signInRequest)
                }
                // Then
                verify(exactly = 1) { userRepository.findByEmail(signInRequest.email) }

                exception.statusCode shouldBeEqual HttpStatus.UNAUTHORIZED
                exception.reason shouldBeEqual "아이디 또는 비밀번호가 올바르지 않습니다."
            }
        }
    }

    "기능] 리프레시" - {
        "2XX] 리프레시 성공" - {
            // Given
            val authHeader = "Bearer ThisIsValidAuthHeader"
            val refreshToken = "RefreshToken"
            val userAccessTokenDto = UserAccessTokenDto(1L, UserRole.USER_EMAIL)
            val userEntity = UserEntity.builder()
                .id(1L)
                .email("testUser1@test.com")
                .role(UserRole.USER_EMAIL)
                .nickName("tester")
                .userImg("/img/2025/05/05/202505.webp")
                .build()

            every { userMapper.toAccessTokenDto(any<Long>(), any<String>()) } returns userAccessTokenDto
            every { jwtUtil.generateAccessToken(userAccessTokenDto) } returns "RenewAccessToken"
            every { jwtUtil.getUserIdFromToken(any<String>()) } returns 1L
            every { jwtUtil.getUserRoleFromToken(any<String>()) } returns "USER_EMAIL"
            every { EncryptUtil.generateRefreshToken() } returns "RenewRefreshToken"
            every { userRepository.findById(any<Long>()) } returns Optional.of(userEntity)

            // When
            val result = userService.refresh(authHeader, refreshToken)

            // Then
            "유효한 Access Token 재발급" {
                result.get("accessToken") shouldBe "RenewAccessToken"
            }
            "유효한 Refresh Token 재발급" {
                result.get("refreshToken") shouldBe "RenewRefreshToken"
            }
        }

        "4XX] AuthHeader가 Bearer로 시작하지 않는 경우" - {
            // Given

            // When

            // Then
        }

        "4XX] 변조된 Access Token인 경우" - {
            // Given

            // When

            // Then
        }

        "4XX] refersh token이 만료된 경우" - {
            // Given

            // WHen

            // Then
        }
    }

    beforeTest {
        userCommandRepository = mockk(relaxed = true)
        oauthCommandRepository = mockk(relaxed = true)
        userCommandMapper = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        jwtUtil = mockk(relaxed = true)
        oauthConfig = mockk(relaxed = true)

        userCommandService = UserCommandService(
            oauthCommandRepository,
            userCommandRepository,
            redisTemplate,
            jwtUtil,
            oauthConfig,
            userCommandMapper
        )
    }
})