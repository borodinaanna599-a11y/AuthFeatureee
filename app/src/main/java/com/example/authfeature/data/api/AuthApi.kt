package com.example.authfeature.data.api

import com.example.authfeature.data.dto.LoginRequest
import com.example.authfeature.data.dto.LoginResponse
import com.example.authfeature.data.dto.RegisterRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): LoginResponse
}
