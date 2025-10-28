package com.meu.stock.views.ui.screens.products.product.create

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meu.stock.model.Product
import com.meu.stock.usecases.DeleteProductUseCase
import com.meu.stock.usecases.GetProductByIdUseCase
import com.meu.stock.usecases.SaveProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToLong

// ViewModel Reescrito para Corrigir o Carregamento de Dados
@HiltViewModel
class ProductFormViewModel @Inject constructor(
    private val saveProductUseCase: SaveProductUseCase,
    private val getProductByIdUseCase: GetProductByIdUseCase,
    private val deleteProductUseCase: DeleteProductUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormState())
    val uiState = _uiState.asStateFlow()

    private val productId: Long? = savedStateHandle.get<String>("productId")?.toLongOrNull()

    init {
        viewModelScope.launch {
            if (productId != null) {
                _uiState.update {
                    it.copy(
                        isEditMode = true,
                        screenTitle = "Editar Produto",
                        isLoading = true
                    )
                }
                getProductByIdUseCase(productId)?.let { product ->
                    _uiState.update {
                        it.copy(
                            name = product.name,
                            description = product.description,
                            stockQuantity = product.stockQuantity.toString(),
                            lowStockQuantity = product.lowStockQuantity.toString(),
                            priceUnit = (product.priceUnit * 100).roundToLong().toString(),
                            priceSale = (product.priceSale * 100).roundToLong().toString(),
                            categoryId = product.categoryId,
                            isLoading = false
                        )
                    }
                } ?: _uiState.update { it.copy(isLoading = false, errorMessage = "Produto não encontrado") }
            } else {
                val categoryId: Long? = savedStateHandle.get<String>("categoryId")?.toLongOrNull()
                _uiState.update { it.copy(categoryId = categoryId) }
            }
        }
    }

    fun onNameChange(newName: String) {
        _uiState.update { it.copy(name = newName) }
    }

    fun onDescriptionChange(newDescription: String) {
        _uiState.update { it.copy(description = newDescription) }
    }

    fun onStockQuantityChange(newQuantity: String) {
        _uiState.update { it.copy(stockQuantity = newQuantity.filter { it.isDigit() }) }
    }

    fun onLowStockQuantityChange(newQuantity: String) {
        _uiState.update { it.copy(lowStockQuantity = newQuantity.filter { it.isDigit() }) }
    }

    fun onPriceUnitChange(newValue: String) {
        _uiState.update { it.copy(priceUnit = newValue.filter { it.isDigit() }) }
    }

    fun onPriceSaleChange(newValue: String) {
        _uiState.update { it.copy(priceSale = newValue.filter { it.isDigit() }) }
    }

    fun onSaveProductClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val currentState = _uiState.value
                val name = currentState.name.ifBlank { throw Exception("O nome do produto é obrigatório.") }
                val categoryId = currentState.categoryId ?: throw Exception("ID da categoria não encontrado.")

                val product = Product(
                    id = productId,
                    name = name,
                    description = currentState.description,
                    stockQuantity = currentState.stockQuantity.toIntOrNull() ?: 0,
                    lowStockQuantity = currentState.lowStockQuantity.toIntOrNull() ?: 0,
                    priceUnit = (currentState.priceUnit.toLongOrNull() ?: 0L) / 100.0,
                    priceSale = (currentState.priceSale.toLongOrNull() ?: 0L) / 100.0,
                    categoryId = categoryId,
                    categoryName = ""
                )

                saveProductUseCase(product)
                _uiState.update { it.copy(isLoading = false, saveSuccess = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onDeleteProductClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onConfirmDelete() {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = false, isLoading = true) }
            try {
                productId?.let {
                    deleteProductUseCase(it)
                    _uiState.update { it.copy(isLoading = false, deleteSuccess = true) }
                } ?: throw Exception("ID do produto inválido para exclusão")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }
}
