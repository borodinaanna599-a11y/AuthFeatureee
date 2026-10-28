package com.example.authfeature.domain.repository

import com.example.authfeature.domain.model.User

/** Контракт репозитория. Реализация живёт в data-слое. */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(email: String, password: String): Result<User>
    suspend fun logout()
}
