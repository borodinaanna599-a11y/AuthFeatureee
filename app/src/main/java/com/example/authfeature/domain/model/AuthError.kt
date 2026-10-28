package com.example.authfeature.domain.model

/** Доменные ошибки авторизации. Ничего не знают про Android и Retrofit. */
sealed class AuthError(message: String? = null) : Exception(message) {
    object InvalidEmail : AuthError()
    object EmptyPassword : AuthError()
    object InvalidCredentials : AuthError()
    object PasswordTooShort : AuthError()
    object PasswordsDoNotMatch : AuthError()
    object EmailAlreadyExists : AuthError()
    object NoNetwork : AuthError()
    class Unknown(reason: String?) : AuthError(reason)
}
