package com.example.authfeature.domain.repository

import com.example.authfeature.domain.model.Note
import kotlinx.coroutines.flow.Flow

/** Контракт репозитория заметок. Реализация — в data-слое. */
interface NoteRepository {
    fun getNotes(): Flow<List<Note>>
    suspend fun insert(note: Note)
    suspend fun delete(note: Note)
    suspend fun syncWithCloud()
}
