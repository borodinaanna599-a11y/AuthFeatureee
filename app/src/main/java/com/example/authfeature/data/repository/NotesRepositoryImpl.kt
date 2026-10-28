package com.example.authfeature.data.repository

import com.example.authfeature.data.api.NotesApi
import com.example.authfeature.data.database.NotesDao
import com.example.authfeature.data.database.toDomain
import com.example.authfeature.data.database.toEntity
import com.example.authfeature.domain.model.Note
import com.example.authfeature.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Паттерн Repository: вызывающая сторона не знает, откуда данные.
 * Источник правды — БД (Room); сеть только обновляет её.
 */
class NotesRepositoryImpl @Inject constructor(
    private val dao: NotesDao,
    private val api: NotesApi
) : NoteRepository {

    override fun getNotes(): Flow<List<Note>> =
        dao.getAllNotes().map { list -> list.map { it.toDomain() } }

    override suspend fun insert(note: Note) = dao.insert(note.toEntity())

    override suspend fun delete(note: Note) = dao.deleteById(note.id)

    override suspend fun syncWithCloud() {
        val remote = api.getNotes()
        dao.insertAll(remote.map { it.toEntity() })
    }
}
