package com.meu.stock.views.ui.screens.notes.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.usecases.GetNotesUseCase

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteListViewModel @Inject constructor(
    private val getNotesUseCase: GetNotesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(NoteListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadNotes()
    }

    private fun loadNotes() {
        viewModelScope.launch {
            getNotesUseCase()
                .onStart {
                    // Antes de começar a coletar, define o estado como 'carregando'.
                    _uiState.update { it.copy(isLoading = true, errorMessage = null) }
                }
                .catch { throwable ->
                    // Em caso de erro no fluxo, atualiza o estado com uma mensagem.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Falha ao carregar as notas."
                        )
                    }
                }
                .collect { notes ->
                    // Quando a lista de notas é recebida com sucesso.
                    _uiState.update {
                        it.copy(isLoading = false, notes = notes)
                    }
                }
        }
    }
}
