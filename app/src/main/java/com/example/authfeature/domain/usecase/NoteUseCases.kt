package com.example.authfeature.domain.usecase

import com.example.authfeature.domain.model.Note
import com.example.authfeature.domain.model.NoteError
import com.example.authfeature.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GetNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    operator fun invoke(): Flow<List<Note>> = repo.getNotes()
}

/** Пример Use Case из карты: валидация + вставка. Можно тестировать на JUnit без эмулятора. */
class AddNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(title: String, text: String): Result<Unit> {
        if (title.isBlank()) return Result.failure(NoteError.EmptyTitle)
        if (text.isBlank()) return Result.failure(NoteError.EmptyContent)
        val note = Note(title = title.trim(), content = text.trim())
        return try {
            repo.insert(note)
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

class DeleteNoteUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(note: Note) = repo.delete(note)
}

class SyncNotesUseCase @Inject constructor(private val repo: NoteRepository) {
    suspend operator fun invoke(): Result<Unit> = try {
        repo.syncWithCloud()
        Result.success(Unit)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
