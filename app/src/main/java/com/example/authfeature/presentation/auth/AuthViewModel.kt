package com.example.authfeature.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.authfeature.R
import com.example.authfeature.domain.model.AuthError
import com.example.authfeature.domain.usecase.AuthUseCase
import com.example.authfeature.domain.usecase.EmailValidator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCase: AuthUseCase,
    private val emailValidator: EmailValidator
) : ViewModel() {

    // Private mutable state
    private val _uiState = MutableStateFlow(AuthUiState())

    // Public immutable state для Composable
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // Кнопка активна, только если email валиден (трансформация потока через .map)
    val isSubmitEnabled: StateFlow<Boolean> = _uiState
        .map { state -> emailValidator.isValid(state.email) && !state.isLoading }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun onEmailChange(value: String) = _uiState.update { it.copy(email = value) }

    fun onPasswordChange(value: String) = _uiState.update { it.copy(password = value) }

    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value) }

    /** Переключение между режимами "Вход" и "Регистрация". */
    fun onToggleModeClick() = _uiState.update {
        it.copy(isRegisterMode = !it.isRegisterMode, confirmPassword = "", error = null)
    }

    fun onSubmitClick() {
        val state = _uiState.value
        if (state.isLoading) return

        // viewModelScope: запрос автоматически отменяется при уничтожении ViewModel
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val result = if (state.isRegisterMode) {
                authUseCase.register(state.email, state.password, state.confirmPassword)
            } else {
                authUseCase.login(state.email, state.password)
            }
            result.fold(
                onSuccess = { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.toUiText()) }
                }
            )
        }
    }

    fun onErrorShown() = _uiState.update { it.copy(error = null) }

    fun onLogoutClick() {
        viewModelScope.launch {
            authUseCase.logout()
            _uiState.value = AuthUiState()
        }
    }

    private fun Throwable.toUiText(): UiText = when (this) {
        AuthError.InvalidEmail -> UiText.Resource(R.string.error_invalid_email)
        AuthError.EmptyPassword -> UiText.Resource(R.string.error_empty_password)
        AuthError.PasswordTooShort -> UiText.Resource(R.string.error_password_too_short)
        AuthError.PasswordsDoNotMatch -> UiText.Resource(R.string.error_passwords_mismatch)
        AuthError.EmailAlreadyExists -> UiText.Resource(R.string.error_email_exists)
        AuthError.InvalidCredentials -> UiText.Resource(R.string.error_invalid_credentials)
        AuthError.NoNetwork -> UiText.Resource(R.string.error_network)
        else -> UiText.Resource(R.string.error_unknown)
    }
}
