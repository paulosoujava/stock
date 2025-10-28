package com.meu.stock.views.ui.screens.products.category.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.contracts.ICategoryRepository
import com.meu.stock.contracts.IProductRepository
import com.meu.stock.model.Category
import com.meu.stock.usecases.GetCategoriesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val categoryRepository: ICategoryRepository, // Renomeado para clareza
    private val productRepository: IProductRepository  // <- DEPENDÊNCIA ADICIONADA
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryListState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            getCategoriesUseCase()
                .onStart { _uiState.update { it.copy(isLoading = true) } }
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { categories ->
                    _uiState.update {
                        it.copy(
                            categories = categories,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onShowDeleteDialog(category: Category) {
        _uiState.update { it.copy(categoryToDelete = category) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(categoryToDelete = null) }
    }

    fun onConfirmDelete() {
        _uiState.value.categoryToDelete?.let { category ->
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }
                try {
                    // 1. Deleta os produtos PRIMEIRO, usando o repositório de produtos
                    productRepository.deleteProduct(category.id!!.toLong())

                    // 2. Em seguida, deleta a categoria, usando o repositório de categorias
                    categoryRepository.deleteCategory(category.id!!.toLong())

                } catch (e: Exception) {
                    _uiState.update { it.copy(error = "Falha ao deletar: ${e.message}") }
                } finally {
                    _uiState.update { it.copy(isLoading = false, categoryToDelete = null) }
                }
            }
        }
    }
}
