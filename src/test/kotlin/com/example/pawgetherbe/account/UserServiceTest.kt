package com.example.pawgetherbe.account
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.dto.UserDto
    import com.example.pawgetherbe.controller.dto.UserDto.*
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.domain.status.UserStatus
import com.example.pawgetherbe.mapper.UserMapper
import com.example.pawgetherbe.repository.OauthRepository
import com.example.pawgetherbe.repository.UserRepository
import com.example.pawgetherbe.service.UserService
import com.example.pawgetherbe.util.EncryptUtil
import com.example.pawgetherbe.util.EncryptUtil.passwordEncode
import com.example.pawgetherbe.util.JwtUtil
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.equals.shouldBeEqual
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.server.ResponseStatusException
import java.time.Duration
import java.util.*

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class UserServiceTest: FreeSpec({
    lateinit var userRepository: UserRepository
    lateinit var oauthRepository: OauthRepository
    lateinit var userMapper: UserMapper
    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var jwtUtil: JwtUtil
    lateinit var oauthConfig: OauthConfig
    lateinit var userService: UserService

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

            every { userMapper.toUserEntity(signUpRequest) } returns entity
            every { userRepository.save(any()) } returns savedEntity

            // When
            userService.signUp(signUpRequest)

            // Then
            val slot = slot<UserEntity>()
            verify { userRepository.save(capture(slot)) }

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

            every { userMapper.toUserEntity(signUpRequest) } returns entity
            every { userRepository.existsByEmail("test@example.com") } returns true

            // When & Then
            val exception = shouldThrow<ResponseStatusException> {
                userService.signUp(signUpRequest)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe "이미 가입된 계정입니다"

            verify(exactly = 0) { userRepository.save(any()) }
        }
    }

    "이메일 중복 검사" - {
        "이메일 중복되지 않을떄" {
            val email = "test@example.com"

            every { userRepository.existsByEmail(email) } returns false

            userService.signupEmailCheck(email)
        }

        "중복 이메일이면 예외 발생" {
            val email = "test@example.com"

            every { userRepository.existsByEmail(email) } returns true

            val exception = shouldThrow<ResponseStatusException> {
                userService.signupEmailCheck(email)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe email + " 이미 가입된 계정입니다."
        }
    }

    "닉네임 중복 검사" - {
        "닉네임이 중복되지 않을때" {
            val nickName = "tester"

            every { userRepository.existsByNickName(nickName) } returns false

            userService.signupNicknameCheck(nickName)
        }

        "중복 닉네임이면 예외 발생" {
            val nickName = "tester"

            every { userRepository.existsByNickName(nickName) } returns true

            val exception = shouldThrow<ResponseStatusException> {
                userService.signupNicknameCheck(nickName)
            }

            exception.statusCode shouldBe HttpStatus.CONFLICT
            exception.reason shouldBe nickName + " 이미 존재하는 닉네임입니다."
        }
    }

    "회원 탈퇴" - {
        "이메일 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { UserContext.getUserId() } returns "1"
            every { oauthRepository.existsByUser_Id(id) } returns false
            every { userRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userService.deleteAccount(refreshToken)

            verify { userRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }

        "간편 로그인 가입자" {
            mockkStatic(UserContext::class)
            every { UserContext.getUserId() } returns "1"

            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"

            every { oauthRepository.existsByUser_Id(id) } returns true
            every { oauthRepository.deleteByUser_Id(id) } just Runs
            every { userRepository.deleteById(id) } just Runs
            every { redisTemplate.delete(refreshToken) } returns true

            userService.deleteAccount(refreshToken)

            verify { oauthRepository.deleteByUser_Id(1L) }
            verify { userRepository.deleteById(1L) }
            verify { redisTemplate.delete(refreshToken) }
        }
    }

    "로그아웃" - {
        "refreshToken 삭제 성공" {
            val refreshToken = "refreshToken123"

            every { redisTemplate.delete(refreshToken) } returns true

            userService.signOut(refreshToken)

            verify { redisTemplate.delete(refreshToken) }
        }
        "refreshToken 삭제 실패" {
            val refreshToken = "refreshToken123"
            every { redisTemplate.delete(refreshToken) } returns false

            userService.signOut(refreshToken)

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

            every { userRepository.findById(id) } returns Optional.of(user)
            every { jwtUtil.generateAccessToken(any()) } returns "newAccessToken"
            every { EncryptUtil.generateRefreshToken() } returns "newRefreshToken"

            val result = userService.updateUserInfo(
                UserDto.UpdateUserRequest(newNickname, newUserImg),
            )
            verify {
                userRepository.findById(id)
                user.updateProfile(newNickname, newUserImg)
            }
            result.userImg shouldBe newUserImg
            result.nickName shouldBe newNickname
        }
        "업데이트 실패" {
            mockkStatic(UserContext::class)
            mockkStatic(EncryptUtil::class)

            every { UserContext.getUserId() } returns "1"
            val id = UserContext.getUserId().toLong()
            val refreshToken = "refreshToken123"
            val newNickname = "newNick"
            val newUserImg = "newImgUrl"

            every { userRepository.findById(id) } returns Optional.empty()

            val exception = shouldThrow<ResponseStatusException> {
                userService.updateUserInfo(
                    UserDto.UpdateUserRequest(newNickname, newUserImg),
                )
            }

            verify(exactly = 0) { redisTemplate.delete(refreshToken) }
            exception.statusCode shouldBe HttpStatus.NOT_FOUND
            exception.reason shouldBe " 존재하지 않는 계정입니다."
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
        userRepository = mockk(relaxed = true)
        oauthRepository = mockk(relaxed = true)
        userMapper = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        jwtUtil = mockk(relaxed = true)
        oauthConfig = mockk(relaxed = true)

        userService = UserService(
            userRepository,
            oauthRepository,
            userMapper,
            redisTemplate,
            jwtUtil,
            oauthConfig
        )
    }
})
