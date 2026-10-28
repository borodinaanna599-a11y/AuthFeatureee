package com.example.authfeature.data.dto

import com.example.authfeature.domain.model.User

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val name: String,
    val email: String
) {
    fun toDomain() = User(name = name, email = email, token = token)
}
