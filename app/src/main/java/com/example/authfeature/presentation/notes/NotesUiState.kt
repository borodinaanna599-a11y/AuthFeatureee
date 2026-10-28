package com.example.authfeature.presentation.notes

import com.example.authfeature.domain.model.Note
import com.example.authfeature.presentation.auth.UiText

/** Всё, что видит пользователь на экране заметок. */
data class NotesUiState(
    val isLoading: Boolean = false,
    val notes: List<Note> = emptyList(),
    val errorMessage: UiText? = null,
    val isAddDialogVisible: Boolean = false,
    val addNoteError: UiText? = null
)
