package com.meu.stock.views.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.meu.stock.contracts.IAuthRepository
import com.meu.stock.contracts.ISaleHistoryRepository
import com.meu.stock.usecases.categories.GetCategoryCountUseCase
import com.meu.stock.usecases.stock.GetLowStockProductsUseCase
import com.meu.stock.usecases.note.GetNoteCountUseCase
import com.meu.stock.usecases.product.GetProductCountUseCase
import com.meu.stock.usecases.clients.GetTotalClientsUseCase
import com.meu.stock.views.ui.routes.AppRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getClientCountUseCase: GetTotalClientsUseCase,
    private val getCategoryCountUseCase: GetCategoryCountUseCase,
    private val getProductCountUseCase: GetProductCountUseCase,
    private val  saleHistoryRepository: ISaleHistoryRepository,
    private val getLowStockProductsUseCase: GetLowStockProductsUseCase,
    private val getNoteCountUseCase: GetNoteCountUseCase,
    private val authRepository: IAuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState = _uiState.asStateFlow()

    private val currencyFormatter = java.text.NumberFormat.getCurrencyInstance(java.util.Locale("pt", "BR"))



    val clientCount: StateFlow<Int> = getClientCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val productCount: StateFlow<Int> = getProductCountUseCase()
        .stateIn(
            scope = viewModelScope,
            // Inicia o Flow quando a UI está observando e para 5s depois.
            started = SharingStarted.WhileSubscribed(5000),
            // Valor inicial enquanto o Flow real não emite o primeiro valor.
            initialValue = 0
        )

    val categoryCount: StateFlow<Int> = getCategoryCountUseCase()
        .stateIn(
            scope = viewModelScope,
            // Inicia o Flow quando a UI está observando e para 5 segundos depois.
            started = SharingStarted.WhileSubscribed(5000),
            // Valor inicial enquanto o Flow real não emite o primeiro valor.
            initialValue = 0
        )

    val noteCount: StateFlow<Int> = getNoteCountUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )


    init {
        // Lança uma coroutine para coletar o resumo de vendas
        viewModelScope.launch {
            saleHistoryRepository.getCurrentMonthSummary().collect { summary ->
                _uiState.update { currentState ->
                    currentState.copy(
                        totalSalesFormatted = currencyFormatter.format(summary.totalSales),
                        currentPeriod = "${summary.month} de ${summary.year}",
                        salesCount = summary.salesCount
                    )
                }
            }
        }

        // Lança uma coroutine para coletar os produtos com estoque baixo
        viewModelScope.launch {
            getLowStockProductsUseCase().collect { lowStockList ->
                _uiState.update { currentState ->
                    currentState.copy(
                        lowStockProducts = lowStockList,
                        // Atualiza também a contagem, se você ainda usa em algum lugar
                        lowStockItemsCount = lowStockList.size
                    )
                }
            }
        }
    }

    fun onCardClicked(cardTitle: String, navController: NavController) {
        when (cardTitle) {
            AppRoutes.CLIENT_LIST -> navController.navigate("client_list?isSelectionMode=false")
            AppRoutes.SALES_YEAR_LIST -> navController.navigate(AppRoutes.SALES_YEAR_LIST)
            AppRoutes.PRODUCT_FORM -> navController.navigate(AppRoutes.PRODUCT_FORM)
            AppRoutes.CATEGORY_LIST -> navController.navigate(AppRoutes.CATEGORY_LIST)
            AppRoutes.LIST_BY_CATEGORY -> navController.navigate(AppRoutes.LIST_BY_CATEGORY)
            AppRoutes.LIVE_LIST -> navController.navigate(AppRoutes.LIVE_LIST)
            AppRoutes.SALE_FORM -> navController.navigate(AppRoutes.SALE_FORM)
            AppRoutes.BEST_SELLERS -> navController.navigate(AppRoutes.BEST_SELLERS)
            AppRoutes.NOTES -> navController.navigate(AppRoutes.NOTES)
            else -> println("$cardTitle card clicked")
        }
    }




    fun onLogoutIconClick() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun onDismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun onConfirmLogout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(showLogoutDialog = false) }
        }
    }
}
