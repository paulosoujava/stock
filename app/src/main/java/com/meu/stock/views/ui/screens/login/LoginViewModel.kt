package com.meu.stock.views.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.IAuthRepository
import com.meu.stock.contracts.UserStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

/**
 * ViewModel para a tela de login.
 * Gerencia o estado da UI e lida com a lógica de negócio do login.
 */
@HiltViewModel
 class LoginViewModel @Inject constructor(
    private val authRepository: IAuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginState())
     val uiState = _uiState.asStateFlow()

    private val _eventChannel = Channel<LoginEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()


    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorMessage = null) }
    }


    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password, errorMessage = null) }
    }


    private fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            authRepository.login(email, password)
                .onSuccess {
                    checkStatusAndProceed()
                }
                .onFailure { exception ->
                    val errorMessage = exception.message ?: "E-mail ou senha inválidos."
                    _uiState.update { it.copy(isLoading = false, errorMessage = errorMessage) }
                }
        }
    }

    private fun checkStatusAndProceed() {
        viewModelScope.launch {
            when (authRepository.checkUserStatus()) {
                UserStatus.ACTIVE -> {
                    _uiState.update { it.copy(isLoading = false) }
                    _eventChannel.send(LoginEvent.NavigateToMain)
                }

                else -> {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Falha ao verificar o usuário."
                        )
                    }
                }
            }
        }
    }

    fun onLoginClick() {
        val currentState = _uiState.value
        loginUser(currentState.email, currentState.password)
    }
}
