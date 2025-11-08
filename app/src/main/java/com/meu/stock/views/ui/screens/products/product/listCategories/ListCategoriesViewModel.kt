package com.meu.stock.views.ui.screens.products.product.listCategories


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.usecases.categories.GetCategoriesUseCase
import com.meu.stock.usecases.product.GetProductsByCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ListCategoriesViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getProductsByCategoryUseCase: GetProductsByCategoryUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListCategoriesState())
    val uiState = _uiState.asStateFlow()


    init {
        loadCategories()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun loadCategories() {
        // Marca o estado como "carregando".
        _uiState.update { it.copy(isLoading = true) }

        getCategoriesUseCase()
            .flatMapLatest { categories ->
                if (categories.isEmpty()) {
                    flowOf(emptyList())
                } else {
                    val countFlows = categories.map { category ->
                        getProductsByCategoryUseCase(category.id!!.toLong()) // Supondo que o ID não seja nulo
                            .map { productList -> productList.size }
                    }
         combine(countFlows) { countsArray ->
                        categories.mapIndexed { index, category ->
                            category.copy(productCount = countsArray[index])
                        }
                    }
                }
            }
            .onEach { categoriesWithCount ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        categories = categoriesWithCount
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}
