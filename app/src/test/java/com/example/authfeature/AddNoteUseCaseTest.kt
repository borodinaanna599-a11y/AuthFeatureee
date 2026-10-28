package com.example.authfeature

import com.example.authfeature.domain.model.Note
import com.example.authfeature.domain.model.NoteError
import com.example.authfeature.domain.repository.NoteRepository
import com.example.authfeature.domain.usecase.AddNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AddNoteUseCaseTest {

    private class FakeNoteRepository(private val fail: Boolean = false) : NoteRepository {
        val inserted = mutableListOf<Note>()
        override fun getNotes(): Flow<List<Note>> = flowOf(inserted)
        override suspend fun insert(note: Note) {
            if (fail) throw IllegalStateException("db error")
            inserted += note
        }
        override suspend fun delete(note: Note) { inserted -= note }
        override suspend fun syncWithCloud() = Unit
    }

    @Test
    fun `blank title returns EmptyTitle`() = runTest {
        val repo = FakeNoteRepository()
        val result = AddNoteUseCase(repo)("  ", "text")

        assertTrue(result.exceptionOrNull() is NoteError.EmptyTitle)
        assertTrue(repo.inserted.isEmpty())
    }

    @Test
    fun `blank content returns EmptyContent`() = runTest {
        val repo = FakeNoteRepository()
        val result = AddNoteUseCase(repo)("title", "")

        assertTrue(result.exceptionOrNull() is NoteError.EmptyContent)
        assertTrue(repo.inserted.isEmpty())
    }

    @Test
    fun `valid note is trimmed and inserted`() = runTest {
        val repo = FakeNoteRepository()
        val result = AddNoteUseCase(repo)(" Title ", " Text ")

        assertTrue(result.isSuccess)
        assertEquals("Title", repo.inserted.single().title)
        assertEquals("Text", repo.inserted.single().content)
    }

    @Test
    fun `repository exception becomes failure`() = runTest {
        val result = AddNoteUseCase(FakeNoteRepository(fail = true))("Title", "Text")

        assertTrue(result.isFailure)
    }
}
