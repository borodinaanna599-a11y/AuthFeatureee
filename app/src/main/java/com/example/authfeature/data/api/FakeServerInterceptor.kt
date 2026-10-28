package com.example.authfeature.data.api

import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import java.io.IOException

/**
 * Имитация сервера, чтобы приложение работало без реального backend.
 *
 * POST auth/login:
 *  - пароль "123456"            -> 200 OK, успешный вход
 *  - email начинается с offline -> IOException (ошибка сети)
 *  - любой другой пароль        -> 401 Unauthorized
 * POST auth/register:
 *  - email начинается с offline -> IOException (ошибка сети)
 *  - email начинается с taken   -> 409 Conflict (email уже занят)
 *  - любой другой email         -> 200 OK, аккаунт создан и сразу выполнен вход
 * GET notes: возвращает пару "облачных" заметок.
 */
class FakeServerInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath
        return when {
            path.endsWith("auth/login") -> handleLogin(request)
            path.endsWith("auth/register") -> handleRegister(request)
            path.endsWith("notes") -> {
                Thread.sleep(1000)
                respond(request, 200, "OK", CLOUD_NOTES_JSON)
            }
            else -> chain.proceed(request)
        }
    }

    private fun handleLogin(request: Request): Response {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val json = JsonParser.parseString(buffer.readUtf8()).asJsonObject
        val email = json.get("email").asString
        val password = json.get("password").asString

        Thread.sleep(1500) // чтобы был виден ProgressBar

        return when {
            email.startsWith("offline") -> throw IOException("Network is unreachable")
            password == "123456" -> respond(
                request, 200, "OK",
                """{"token":"fake-token-123","name":"${email.substringBefore('@')}","email":"$email"}"""
            )
            else -> respond(request, 401, "Unauthorized", """{"message":"Invalid credentials"}""")
        }
    }

    private fun handleRegister(request: Request): Response {
        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val json = JsonParser.parseString(buffer.readUtf8()).asJsonObject
        val email = json.get("email").asString

        Thread.sleep(1500)

        return when {
            email.startsWith("offline") -> throw IOException("Network is unreachable")
            email.startsWith("taken") -> respond(
                request, 409, "Conflict", """{"message":"Email already exists"}"""
            )
            else -> respond(
                request, 200, "OK",
                """{"token":"fake-token-new","name":"${email.substringBefore('@')}","email":"$email"}"""
            )
        }
    }

    private fun respond(request: Request, code: Int, message: String, body: String) =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(message)
            .body(body.toResponseBody("application/json".toMediaType()))
            .build()

    private companion object {
        const val CLOUD_NOTES_JSON = """[
            {"id":1000001,"title":"Заметка из облака","content":"Эта заметка загружена через Retrofit и сохранена в Room","timestamp":1700000000000},
            {"id":1000002,"title":"Clean Architecture","content":"Presentation -> Domain <- Data","timestamp":1700000100000}
        ]"""
    }
}
