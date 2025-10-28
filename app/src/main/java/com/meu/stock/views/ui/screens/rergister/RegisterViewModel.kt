package com.meu.stock.views.ui.screens.rergister

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.IAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterState())
    val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<RegisterEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }

    fun onConfirmPasswordChange(password: String) {
        _uiState.update { it.copy(confirmPassword = password, errorMessage = null) }
    }

    fun onRegisterClick() {
        val state = _uiState.value
        if (state.password != state.confirmPassword) {
            _uiState.update { it.copy(errorMessage = "As senhas não coincidem.") }
            return
        }

        if (state.email.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "E-mail e senha não podem estar em branco.") }
            return
        }

        registerUser(state.email, state.password)
    }

    private fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.register(email, password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventChannel.send(RegisterEvent.NavigateToMain)
                }
                .onFailure { exception ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = exception.message ?: "Ocorreu um erro no cadastro."
                        )
                    }
                }
        }
    }

    fun onHelpClick() {
        viewModelScope.launch {
            _eventChannel.send(RegisterEvent.ShowHelpDialog)
        }
    }
}
