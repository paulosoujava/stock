package com.meu.stock.views.ui.screens.clients.list

import com.meu.stock.model.Client

data class ClientListState(
    val isLoading: Boolean = false,
    val clients: List<Client> = emptyList(),
    val errorMessage: String? = null,
    val isSearchActive: Boolean = false, // Controla se a barra de pesquisa está visível
    val searchQuery: String = "", // Armazena o texto da pesquisa
    val clientNameToExpand: String? = null
)