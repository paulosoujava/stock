package com.meu.stock.views.ui.screens.sales



import com.meu.stock.model.Client // Supondo que você tenha um modelo de Cliente
import com.meu.stock.model.Product
import com.meu.stock.model.SaleItem

/**
 * Representa o estado da UI para a tela de registro de Venda.
 */
data class SaleFormState(
    // Estado da Pesquisa e Resultados
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),

    // Estado do "Carrinho" de Vendas
    val saleItems: List<SaleItem> = emptyList(),
    val totalAmount: Double = 0.0,

    // Estado do Cliente e outros dados da Venda
    val selectedClient: Client? = null,
    val notes: String = "",

    // Estado de controle da UI
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)
