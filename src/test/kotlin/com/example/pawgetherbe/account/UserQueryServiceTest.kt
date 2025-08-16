package com.example.pawgetherbe.account
import com.example.pawgetherbe.config.OauthConfig
import com.example.pawgetherbe.controller.command.dto.UserCommandDto
import com.example.pawgetherbe.controller.command.dto.UserCommandDto.UserSignUpRequest
import com.example.pawgetherbe.domain.UserContext
import com.example.pawgetherbe.domain.entity.UserEntity
import com.example.pawgetherbe.domain.status.UserRole
import com.example.pawgetherbe.domain.status.UserStatus
import com.example.pawgetherbe.mapper.command.UserCommandMapper
import com.example.pawgetherbe.mapper.query.UserQueryMapper
import com.example.pawgetherbe.repository.command.OauthCommandRepository
import com.example.pawgetherbe.repository.command.UserCommandRepository
import com.example.pawgetherbe.repository.query.UserQueryRepository
import com.example.pawgetherbe.service.command.UserCommandService
import com.example.pawgetherbe.service.query.UserQueryService
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
class UserQueryServiceTest: FreeSpec({
    lateinit var userQueryRepository: UserQueryRepository
    lateinit var userQueryMapper: UserQueryMapper
    lateinit var redisTemplate: RedisTemplate<String, String>
    lateinit var jwtUtil: JwtUtil
    lateinit var userQueryService: UserQueryService

    "이메일 중복 검사" - {
        "이메일 중복되지 않을떄" {
            val email = "test@example.com"

            every { userQueryRepository.existsByEmail(email) } returns false

            userQueryService.signupEmailCheck(email)
        }

        "중복 이메일이면 예외 발생" {
            val email = "test@example.com"

            every { userQueryRepository.existsByEmail(email) } returns true

            val exception = shouldThrow<RuntimeException> {
                userQueryService.signupEmailCheck(email)
            }

            exception.message shouldBe "이미 존재하는 Email 입니다."
        }
    }

    "닉네임 중복 검사" - {
        "닉네임이 중복되지 않을때" {
            val nickName = "tester"

            every { userQueryRepository.existsByNickName(nickName) } returns false

            userQueryService.signupNicknameCheck(nickName)
        }

        "중복 닉네임이면 예외 발생" {
            val nickName = "tester"

            every { userQueryRepository.existsByNickName(nickName) } returns true

            val exception = shouldThrow<RuntimeException> {
                userQueryService.signupNicknameCheck(nickName)
            }

            exception.message shouldBe "이미 존재하는 NickName 입니다."
        }
    }



    beforeTest {
        userQueryRepository = mockk(relaxed = true)
        userQueryMapper = mockk(relaxed = true)
        redisTemplate = mockk(relaxed = true)
        jwtUtil = mockk(relaxed = true)

        userQueryService = UserQueryService(
            redisTemplate,
            jwtUtil,
            userQueryRepository,
            userQueryMapper
        )
    }
})