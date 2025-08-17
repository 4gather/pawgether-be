package com.example.pawgetherbe.account
import com.example.pawgetherbe.repository.query.UserQueryRepository
import com.example.pawgetherbe.service.query.UserQueryService
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ActiveProfiles("test")
@ExtendWith(SpringExtension::class)
class UserQueryServiceTest: FreeSpec({
    lateinit var userQueryRepository: UserQueryRepository
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

        userQueryService = UserQueryService(
            userQueryRepository
        )
    }
})