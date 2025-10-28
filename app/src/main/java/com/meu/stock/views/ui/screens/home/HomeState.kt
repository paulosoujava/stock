package com.meu.stock.views.ui.screens.home

import com.meu.stock.model.Product

/**
 * Represents the UI state for the Home screen.
 * Currently empty, but can be expanded with user data, lists, etc.
 */
data class HomeState(
    val lowStockProducts: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val showLogoutDialog: Boolean = false,
    val lowStockItemsCount: Int = 0,
    val totalClients: Int = 0,
    val totalSalesFormatted: String = "R$ 0,00",
    val currentPeriod: String = "Mês/Ano",
    val salesCount: Int = 0,
    val noteCount: Int = 0,
)
