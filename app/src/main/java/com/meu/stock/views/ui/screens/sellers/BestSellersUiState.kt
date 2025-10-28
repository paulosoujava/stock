package com.meu.stock.views.ui.screens.sellers

import com.meu.stock.model.BestSellingProductInfo


/**
 * Representa o estado da tela de "Mais Vendidos".
 */
data class BestSellersUiState(
    val isLoading: Boolean = true,
    // 2. SUBSTITUA O TIPO DA LISTA PELO MODELO NOVO E ENRIQUECIDO
    val bestSellers: List<BestSellingProductInfo> = emptyList(),
    val errorMessage: String? = null
)