package com.example.authfeature.domain.model

/** Бизнес-сущность: чистый Kotlin-класс, без Room и Android. */
data class Note(
    val id: Int = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
