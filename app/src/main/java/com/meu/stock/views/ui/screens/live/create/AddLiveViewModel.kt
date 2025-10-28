package com.meu.stock.views.ui.screens.live.create


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddLiveViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(AddLiveState())
    val uiState = _uiState.asStateFlow()

    // Funções de atualização para cada campo do formulário
    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value) }
    fun onDateChange(value: String) = _uiState.update { it.copy(startDate = value) }
    fun onTimeChange(value: String) = _uiState.update { it.copy(startTime = value) }

    /**
     * Acionado ao clicar no botão para salvar a live.
     * Valida os campos e simula uma chamada de API.
     */
    fun onSaveLiveClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1500) // Simula chamada de rede

            val state = _uiState.value
            if (state.title.isBlank() || state.startDate.isBlank() || state.startTime.isBlank()) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Todos os campos são obrigatórios."
                    )
                }
            } else {
                println("Salvando Live: $state")
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            }
        }
    }
}
