package com.example.authfeature.data.dto

import com.example.authfeature.data.database.NoteEntity

/** Сетевая модель (JSON mapping). */
data class NoteDto(
    val id: Int,
    val title: String,
    val content: String,
    val timestamp: Long
) {
    fun toEntity() = NoteEntity(id = id, title = title, content = content, timestamp = timestamp)
}
