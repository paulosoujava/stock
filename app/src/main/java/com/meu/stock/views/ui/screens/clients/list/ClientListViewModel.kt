package com.meu.stock.views.ui.screens.clients.list


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.meu.stock.contracts.IClientRepository
import com.meu.stock.contracts.IGetClientsUseCase
import com.meu.stock.model.Client
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.contains

@HiltViewModel
class ClientListViewModel @Inject constructor(
    private val clientRepository: IClientRepository,
    private val getClientsUseCase: IGetClientsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ClientListState())
    val uiState = _uiState.asStateFlow()

    private val clientsFlow: Flow<List<Client>> = getClientsUseCase().map { clients ->
        clients.sortedBy { it.name }
    }



    init {

        viewModelScope.launch {
            combine(clientsFlow, _uiState) { clients, state ->
                val filteredList = if (state.searchQuery.isBlank()) {
                    clients
                } else {
                    clients.filter { client ->
                        client.name.contains(state.searchQuery, ignoreCase = true) ||
                                client.phone.contains(state.searchQuery, ignoreCase = true) ||
                                client.cpf.contains(state.searchQuery, ignoreCase = true) ||
                                client.email.contains(state.searchQuery, ignoreCase = true)
                    }
                }
                // 3. O ERRO SOME: `filteredList` é List<Client> e `state.copy` espera List<Client>
                state.copy(clients = filteredList, isLoading = false)
            }
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { throwable ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = throwable.message) }
                }
                .collect { newState ->
                    _uiState.value = newState
                }
        }
    }


    fun searchThisClientNavigation(preSearchQuery: String?) {
        if (!preSearchQuery.isNullOrBlank()) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isSearchActive = true,
                        searchQuery = preSearchQuery,
                        // Opcional, mas recomendado: avisa a UI qual cliente expandir
                        clientNameToExpand = preSearchQuery
                    )
                }
            }
        }
    }


    fun toggleSearchVisibility() {
        _uiState.update { currentState ->
            val isVisible = currentState.isSearchActive
            currentState.copy(
                isSearchActive = !isVisible,
                searchQuery = if (isVisible) "" else currentState.searchQuery,
                clientNameToExpand = if (isVisible) null else currentState.clientNameToExpand
            )
        }

    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun deleteClient(clientId: String) {
        viewModelScope.launch {
            clientRepository.delete(clientId)
        }
    }
}
