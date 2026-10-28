package com.example.authfeature.domain.usecase

import javax.inject.Inject

/** Чистая Kotlin-валидация (без android.util.Patterns), поэтому тестируется на JUnit. */
class EmailValidator @Inject constructor() {
    private val regex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

    fun isValid(email: String): Boolean = regex.matches(email.trim())
}
