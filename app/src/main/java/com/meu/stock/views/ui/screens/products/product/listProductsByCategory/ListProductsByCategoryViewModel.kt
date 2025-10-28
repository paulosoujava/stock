package com.meu.stock.views.ui.screens.products.product.listProductsByCategory

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Product
import com.meu.stock.usecases.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ListProductsByCategoryViewModel @Inject constructor(
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListProductsByCategoryState())
    val uiState = _uiState.asStateFlow()
    private var originalProducts: List<Product> = emptyList()

    init {
        val categoryId: Long? = savedStateHandle["categoryId"]
        val categoryName: String? = savedStateHandle["categoryName"]

        _uiState.update {
            it.copy(
                categoryId = categoryId ?: -1L,
                categoryName = categoryName ?: it.categoryName
            )
        }

        if (categoryId != null && categoryId != -1L) {
            loadProducts(categoryId)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadProducts(categoryId: Long) {
        getProductsByCategoryUseCase(categoryId)
            .onEach { productList ->
                // Salva a lista original
                originalProducts = productList
                _uiState.update {
                    it.copy(
                        products = productList, // opcional, se ainda precisar da lista completa
                        filteredProducts = productList, // começa mostrando todos
                        isLoading = false
                    )
                }
            }
            .launchIn(viewModelScope)
    }


    fun toggleSearchVisibility() {
        _uiState.update { currentState ->
            val isVisible = currentState.isSearchActive
            // Limpa a busca e restaura a lista quando a barra de busca é fechada
            val filteredList = if (isVisible) originalProducts else currentState.filteredProducts
            currentState.copy(
                isSearchActive = !isVisible,
                searchQuery = if (isVisible) "" else currentState.searchQuery,
                filteredProducts = filteredList
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { currentState ->
            val filteredList = if (query.isBlank()) {
                // Se a busca estiver vazia, mostre todos os produtos originais
                originalProducts
            } else {
                // Filtra a lista original
                originalProducts.filter { product ->
                    product.name.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true)
                }
            }
            currentState.copy(
                searchQuery = query,
                filteredProducts = filteredList
            )
        }
    }
}