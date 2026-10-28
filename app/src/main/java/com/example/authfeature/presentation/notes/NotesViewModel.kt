package com.example.authfeature.presentation.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authfeature.R
import com.example.authfeature.domain.model.Note
import com.example.authfeature.domain.model.NoteError
import com.example.authfeature.domain.usecase.AddNoteUseCase
import com.example.authfeature.domain.usecase.DeleteNoteUseCase
import com.example.authfeature.domain.usecase.GetNotesUseCase
import com.example.authfeature.domain.usecase.SyncNotesUseCase
import com.example.authfeature.presentation.auth.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val getNotes: GetNotesUseCase,
    private val addNote: AddNoteUseCase,
    private val deleteNote: DeleteNoteUseCase,
    private val syncNotes: SyncNotesUseCase
) : ViewModel() {

    // Private mutable state
    private val _uiState = MutableStateFlow(NotesUiState())

    // Public immutable state для Composable
    val uiState: StateFlow<NotesUiState> = _uiState.asStateFlow()

    init {
        observeNotes()
        loadNotes()
    }

    /** Подписка на БД: любое изменение таблицы мгновенно попадает в UiState. */
    private fun observeNotes() {
        getNotes()
            .onEach { list -> _uiState.update { it.copy(notes = list) } }
            .launchIn(viewModelScope)
    }

    /** Синхронизация с "облаком" (Retrofit -> Room). */
    fun loadNotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            syncNotes().onFailure { e ->
                _uiState.update { it.copy(errorMessage = e.toSyncUiText()) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onAddClick() = _uiState.update { it.copy(isAddDialogVisible = true, addNoteError = null) }

    fun onDialogDismiss() = _uiState.update { it.copy(isAddDialogVisible = false, addNoteError = null) }

    fun onSaveNote(title: String, content: String) {
        viewModelScope.launch {
            addNote(title, content).fold(
                onSuccess = {
                    _uiState.update { it.copy(isAddDialogVisible = false, addNoteError = null) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(addNoteError = e.toAddNoteUiText()) }
                }
            )
        }
    }

    fun onDeleteClick(note: Note) {
        viewModelScope.launch { deleteNote(note) }
    }

    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }

    private fun Throwable.toSyncUiText(): UiText = when (this) {
        is IOException -> UiText.Resource(R.string.error_sync_network)
        else -> UiText.Resource(R.string.error_unknown)
    }

    private fun Throwable.toAddNoteUiText(): UiText = when (this) {
        NoteError.EmptyTitle -> UiText.Resource(R.string.error_empty_title)
        NoteError.EmptyContent -> UiText.Resource(R.string.error_empty_content)
        else -> UiText.Resource(R.string.error_unknown)
    }
}
