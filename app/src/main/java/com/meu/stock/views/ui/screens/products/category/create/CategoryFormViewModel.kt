package com.meu.stock.views.ui.screens.products.category.create

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Category
import com.meu.stock.usecases.GetCategoryByIdUseCase
import com.meu.stock.usecases.SaveCategoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryFormViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val getCategoryByIdUseCase: GetCategoryByIdUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryFormState())
    val uiState = _uiState.asStateFlow()

    private val categoryId: Long? = savedStateHandle["categoryId"]


    init {
        Log.d("LOG", "categoryId: $categoryId")
        if (categoryId != null && categoryId != -1L) {
            loadCategory(categoryId)
        } else {
            viewModelScope.launch {
                _uiState.emit(CategoryFormState()) // Reseta para o estado inicial padrão
            }
        }
    }
    private fun loadProductData(id: Long) {
        viewModelScope.launch {
            // val product = productRepository.getProductById(id)
            // Atualize o estado da UI com os dados do produto
        }
    }
    private fun loadCategory(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getCategoryByIdUseCase(id).collect { category ->
                if (category != null) {
                    // Preenche o estado da UI com os dados do banco
                    _uiState.update {
                        it.copy(
                            id = category.id,
                            name = category.name,
                            description = category.description,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun saveCategory() {

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                Log.d("LOG", "Data antes da criação: ${_uiState.value}")
                val idToSave = if (categoryId == -1L) {
                    null
                } else {
                    _uiState.value.id
                }
                val category = Category(
                    id = idToSave,
                    name = _uiState.value.name,
                    description = _uiState.value.description
                )

                Log.d("LOG", "Objeto Category a ser salvo: $category")
                saveCategoryUseCase(category)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
                Log.d("LOG", "Salvo com sucesso: ${_uiState.value}")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
                Log.d(
                    "LOG",
                    "Erro ao salvar: ${_uiState.value}",
                    e
                ) // Adicionado 'e' para logar a exceção
            }
        }
    }
}
