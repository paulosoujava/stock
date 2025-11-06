package com.meu.stock.views.ui.screens.sellers


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.usecases.GetBestSellingItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BestSellersViewModel @Inject constructor(
    private val getBestSellingItemsUseCase: GetBestSellingItemsUseCase,
    private val saleHistoryDao: com.meu.stock.bd.sale.SaleHistoryDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(BestSellersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        debugDatabase()
        loadBestSellers()
    }

    private fun loadBestSellers() {
        viewModelScope.launch {
            getBestSellingItemsUseCase()
                .onStart {
                    // No início da coleta do Flow, ativamos o estado de carregamento.
                    _uiState.update { it.copy(isLoading = true) }
                }
                .catch { throwable ->
                    // Se ocorrer um erro na coleta do Flow (ex: erro no banco de dados).
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = "Falha ao carregar os dados. Tente novamente."
                        )
                    }
                }
                .collect { bestSellersList ->
                    // Dados recebidos com sucesso.
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            bestSellers = bestSellersList
                        )
                    }
                }
        }
    }
    private fun debugDatabase() {
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            val jsonStrings = saleHistoryDao.debugGetRawJsonStrings()
            if (jsonStrings.isEmpty()) {
                android.util.Log.e("DATABASE_DEBUG", "A query não retornou NENHUMA string JSON da tabela 'sales'.")
            } else {
                android.util.Log.d("DATABASE_DEBUG", "JSON Encontrado no Banco: ${jsonStrings.firstOrNull()}")
            }
        }
    }
}
