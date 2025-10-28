package com.meu.stock.views.ui.screens.products.product.listCategories


import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Product
import com.meu.stock.usecases.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListCategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListCategoriesState())
    val uiState = _uiState.asStateFlow()


    init {
        loadCategories()
    }

    private fun loadCategories() {
        // Marca o estado como "carregando".
        _uiState.update { it.copy(isLoading = true) }

        // Chama o UseCase para obter o Flow da lista de categorias.
        getCategoriesUseCase()
            .onEach { categories ->
                // Cada vez que o banco de dados muda, este bloco é executado.
                _uiState.update {
                    it.copy(
                        isLoading = false, // Desliga o loading, pois os dados chegaram.
                        categories = categories
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
