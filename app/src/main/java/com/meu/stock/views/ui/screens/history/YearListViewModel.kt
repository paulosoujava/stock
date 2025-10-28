package com.meu.stock.views.ui.screens.history


import androidx.lifecycle.ViewModel

import androidx.lifecycle.viewModelScope

import com.meu.stock.contracts.ISaleHistoryRepository

import com.meu.stock.model.MonthSummary
import com.meu.stock.model.SalesMonth
import com.meu.stock.model.SalesYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class YearListViewModel @Inject constructor(
    saleHistoryRepository: ISaleHistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(YearListState())

    val uiState: StateFlow<YearListState> = saleHistoryRepository.getSalesHistory()
        .map { yearsList ->
            // Mapeia a lista de 'SalesYear' para o estado da UI
            YearListState(years = yearsList, isLoading = false)
        }
        .onStart {
            // Emite um estado de carregamento no início da coleta
            emit(YearListState(isLoading = true))
        }
        .stateIn(
            scope = viewModelScope,
            // Começa a coletar imediatamente e mantém o último valor
            started = SharingStarted.WhileSubscribed(5000),
            // Valor inicial do estado
            initialValue = YearListState(isLoading = true)
        )


    /**
     * Alterna o estado de expansão de um ano.
     */
    fun onYearClick(year: Int) {
        val updatedYears = _uiState.value.years.map { salesYear ->
            // Se for o ano clicado, inverte o estado 'isExpanded'.
            // Se não for, mantém como está.
            if (salesYear.year == year) {
                salesYear.copy(isExpanded = !salesYear.isExpanded)
            } else {
                salesYear
            }
        }
        _uiState.update { it.copy(years = updatedYears) }
    }

    /**
     * Alterna o estado de expansão de um mês específico dentro de um ano.
     */
    fun onMonthClick(year: Int, monthNumber: Int) {
        val updatedYears = _uiState.value.years.map { salesYear ->
            if (salesYear.year == year) {
                val updatedMonths = salesYear.months.map { salesMonth ->
                    if (salesMonth.monthNumber == monthNumber) {
                        salesMonth.copy(isExpanded = !salesMonth.isExpanded)
                    } else {
                        salesMonth
                    }
                }
                salesYear.copy(months = updatedMonths)
            } else {
                salesYear
            }
        }
        _uiState.update { it.copy(years = updatedYears) }
    }

    // --- Funções de Fechamento de Mês (já estavam corretas) ---

    fun onShowCloseMonthDialog(month: SalesMonth) {
        _uiState.update { it.copy(monthToClose = month) }
    }

    fun onDismissCloseMonthDialog() {
        _uiState.update { it.copy(monthToClose = null) }
    }


}
