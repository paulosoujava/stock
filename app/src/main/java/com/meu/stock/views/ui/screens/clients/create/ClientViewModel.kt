package com.meu.stock.views.ui.screens.clients.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Client
import com.meu.stock.usecases.clients.GetClientByIdUseCase
import com.meu.stock.usecases.clients.SaveClientUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ClientViewModel @Inject constructor(
    private val getClientByIdUseCase: GetClientByIdUseCase,
    private val saveClientUseCase: SaveClientUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientState())
    val uiState = _uiState.asStateFlow()

    fun loadClient(clientId: String?) {
        if (clientId == null) {
            // Modo "criar novo", não faz nada
            return
        }
        viewModelScope.launch {
            getClientByIdUseCase(clientId)
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Erro ao recuperar cliente"
                        )
                    }
                }
                .collect { client ->
                    if (client != null) {
                        // Preenche o estado do formulário com os dados do cliente
                        _uiState.update {
                            it.copy(
                                id = client.id.toString(),
                                fullName = client.name,
                                phone = client.phone,
                                email = client.email,
                                cpf = client.cpf,
                                notes = client.notes,
                                address = client.address,
                                isLoading = false
                            )
                        }
                    }
                }
        }
    }
    fun onFullNameChange(name: String) {
        _uiState.update { it.copy(fullName = name) }
    }

    fun onCpfChange(newValue: String) {
        // Garante que o ViewModel armazene APENAS os dígitos e no máximo 11.
        val digitsOnly = newValue.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(cpf = digitsOnly) }
    }
    fun onPhoneChange(newValue: String) {
        val digitsOnly = newValue.filter { it.isDigit() }.take(11)
        _uiState.update { it.copy(phone = digitsOnly) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onNotesChange(notes: String) {
        _uiState.update { it.copy(notes = notes) }
    }
    fun onAddressChange(address: String){
        _uiState.update { it.copy(address = address) }
    }
    fun onSaveClientClick() {
        viewModelScope.launch {
            val currentState = _uiState.value
            if (currentState.fullName.isBlank()) {
                _uiState.update { it.copy(errorMessage = "O nome completo é obrigatório.") }
                return@launch
            }

            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val client = Client(
                id = currentState.id.toLongOrNull() ?: 0,
                name = currentState.fullName.trim(),
                cpf = currentState.cpf.trim(),
                phone = currentState.phone.trim(),
                email = currentState.email.trim(),
                notes = currentState.notes.trim(),
                address = currentState.address.trim(),
            )
            //para a ediçao
            if(currentState.id.isNotBlank()) {
                client.id = currentState.id.toLong()
            }


            try {
                saveClientUseCase(client)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Erro ao salvar cliente: ${e.message}"
                    )
                }
            }
        }
    }
    
}
