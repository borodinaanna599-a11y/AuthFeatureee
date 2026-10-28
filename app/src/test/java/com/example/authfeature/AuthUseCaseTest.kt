package com.example.authfeature

import com.example.authfeature.domain.model.AuthError
import com.example.authfeature.domain.model.User
import com.example.authfeature.domain.repository.AuthRepository
import com.example.authfeature.domain.usecase.AuthUseCase
import com.example.authfeature.domain.usecase.EmailValidator
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Unit-тест без эмулятора: репозиторий подменяется "мокнутой" реализацией интерфейса. */
class AuthUseCaseTest {

    private class FakeAuthRepository(
        private val result: Result<User> = Result.success(User("john", "john@mail.com", "token"))
    ) : AuthRepository {
        var loginCalls = 0
        override suspend fun login(email: String, password: String): Result<User> {
            loginCalls++
            return result
        }
        var registerCalls = 0
        override suspend fun register(email: String, password: String): Result<User> {
            registerCalls++
            return result
        }
        override suspend fun logout() = Unit
    }

    @Test
    fun `invalid email returns InvalidEmail and does not call repository`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).login("not-an-email", "123456")

        assertTrue(result.exceptionOrNull() is AuthError.InvalidEmail)
        assertEquals(0, repo.loginCalls)
    }

    @Test
    fun `blank password returns EmptyPassword`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).login("john@mail.com", "  ")

        assertTrue(result.exceptionOrNull() is AuthError.EmptyPassword)
        assertEquals(0, repo.loginCalls)
    }

    @Test
    fun `valid data returns user from repository`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).login(" john@mail.com ", "123456")

        assertEquals("john", result.getOrNull()?.name)
        assertEquals(1, repo.loginCalls)
    }

    @Test
    fun `repository error is passed through`() = runTest {
        val repo = FakeAuthRepository(Result.failure(AuthError.NoNetwork))
        val result = AuthUseCase(repo, EmailValidator()).login("john@mail.com", "123456")

        assertTrue(result.exceptionOrNull() is AuthError.NoNetwork)
    }

    @Test
    fun `email validator accepts and rejects expected values`() {
        val v = EmailValidator()
        assertTrue(v.isValid("a.b@site.ru"))
        assertTrue(!v.isValid("a@site"))
        assertTrue(!v.isValid(""))
    }

    @Test
    fun `register with invalid email returns InvalidEmail`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).register("bad", "123456", "123456")

        assertTrue(result.exceptionOrNull() is AuthError.InvalidEmail)
        assertEquals(0, repo.registerCalls)
    }

    @Test
    fun `register with short password returns PasswordTooShort`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).register("john@mail.com", "123", "123")

        assertTrue(result.exceptionOrNull() is AuthError.PasswordTooShort)
        assertEquals(0, repo.registerCalls)
    }

    @Test
    fun `register with different passwords returns PasswordsDoNotMatch`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).register("john@mail.com", "123456", "654321")

        assertTrue(result.exceptionOrNull() is AuthError.PasswordsDoNotMatch)
        assertEquals(0, repo.registerCalls)
    }

    @Test
    fun `register with valid data calls repository`() = runTest {
        val repo = FakeAuthRepository()
        val result = AuthUseCase(repo, EmailValidator()).register(" john@mail.com ", "123456", "123456")

        assertEquals("john", result.getOrNull()?.name)
        assertEquals(1, repo.registerCalls)
    }

    @Test
    fun `register passes EmailAlreadyExists from repository`() = runTest {
        val repo = FakeAuthRepository(Result.failure(AuthError.EmailAlreadyExists))
        val result = AuthUseCase(repo, EmailValidator()).register("john@mail.com", "123456", "123456")

        assertTrue(result.exceptionOrNull() is AuthError.EmailAlreadyExists)
    }
}
