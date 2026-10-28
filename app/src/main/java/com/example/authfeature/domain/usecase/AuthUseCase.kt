package com.example.authfeature.domain.usecase

import com.example.authfeature.domain.model.AuthError
import com.example.authfeature.domain.model.User
import com.example.authfeature.domain.repository.AuthRepository
import javax.inject.Inject

class AuthUseCase @Inject constructor(
    private val repository: AuthRepository,
    private val emailValidator: EmailValidator
) {
    suspend fun login(email: String, password: String): Result<User> {
        val cleanEmail = email.trim()
        if (!emailValidator.isValid(cleanEmail)) return Result.failure(AuthError.InvalidEmail)
        if (password.isBlank()) return Result.failure(AuthError.EmptyPassword)
        return repository.login(cleanEmail, password)
    }

    suspend fun register(email: String, password: String, confirmPassword: String): Result<User> {
        val cleanEmail = email.trim()
        if (!emailValidator.isValid(cleanEmail)) return Result.failure(AuthError.InvalidEmail)
        if (password.length < MIN_PASSWORD_LENGTH) return Result.failure(AuthError.PasswordTooShort)
        if (password != confirmPassword) return Result.failure(AuthError.PasswordsDoNotMatch)
        return repository.register(cleanEmail, password)
    }

    suspend fun logout() = repository.logout()

    companion object {
        const val MIN_PASSWORD_LENGTH = 6
    }
}
