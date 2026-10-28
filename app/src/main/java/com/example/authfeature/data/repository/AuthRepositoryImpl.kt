package com.example.authfeature.data.repository

import com.example.authfeature.data.api.AuthApi
import com.example.authfeature.data.database.UserDao
import com.example.authfeature.data.database.toEntity
import com.example.authfeature.data.dto.LoginRequest
import com.example.authfeature.data.dto.RegisterRequest
import com.example.authfeature.domain.model.AuthError
import com.example.authfeature.domain.model.User
import com.example.authfeature.domain.repository.AuthRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApi,
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = try {
        val user = api.login(LoginRequest(email, password)).toDomain()
        userDao.insert(user.toEntity())
        Result.success(user)
    } catch (e: CancellationException) {
        throw e // не глотаем отмену корутины — иначе ломается Lifecycle-отмена
    } catch (e: HttpException) {
        Result.failure(
            if (e.code() == 401) AuthError.InvalidCredentials else AuthError.Unknown(e.message())
        )
    } catch (e: IOException) {
        Result.failure(AuthError.NoNetwork)
    } catch (e: Exception) {
        Result.failure(AuthError.Unknown(e.message))
    }

    override suspend fun register(email: String, password: String): Result<User> = try {
        val user = api.register(RegisterRequest(email, password)).toDomain()
        userDao.insert(user.toEntity())
        Result.success(user)
    } catch (e: CancellationException) {
        throw e
    } catch (e: HttpException) {
        Result.failure(
            if (e.code() == 409) AuthError.EmailAlreadyExists else AuthError.Unknown(e.message())
        )
    } catch (e: IOException) {
        Result.failure(AuthError.NoNetwork)
    } catch (e: Exception) {
        Result.failure(AuthError.Unknown(e.message))
    }

    override suspend fun logout() = userDao.clear()
}
