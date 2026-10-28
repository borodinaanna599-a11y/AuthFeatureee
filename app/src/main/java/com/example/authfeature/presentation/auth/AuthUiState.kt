package com.example.authfeature.presentation.auth

import com.example.authfeature.domain.model.User

/** Неизменяемое состояние экрана (Immutable State). */
data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isRegisterMode: Boolean = false,
    val isLoading: Boolean = false,
    val error: UiText? = null,
    val user: User? = null
)
