package com.example.authfeature.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.authfeature.presentation.auth.AuthViewModel
import com.example.authfeature.presentation.auth.LoginRoute
import com.example.authfeature.presentation.notes.NotesRoute

/** Нет пользователя -> экран входа, есть -> экран заметок. */
@Composable
fun AppRoot(authViewModel: AuthViewModel = hiltViewModel()) {
    val state by authViewModel.uiState.collectAsStateWithLifecycle()
    if (state.user == null) {
        LoginRoute(authViewModel)
    } else {
        NotesRoute(onLogoutClick = authViewModel::onLogoutClick)
    }
}
