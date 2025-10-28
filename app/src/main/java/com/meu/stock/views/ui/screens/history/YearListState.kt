package com.meu.stock.views.ui.screens.history

import com.meu.stock.model.SalesMonth
import com.meu.stock.model.SalesYear

data class YearListState(
    val isLoading: Boolean = false,
    val years: List<SalesYear> = emptyList(),
    // Guarda o mês que está sendo considerado para fechamento
    val monthToClose: SalesMonth? = null
)