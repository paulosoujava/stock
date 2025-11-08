package com.meu.stock.views.ui.screens.products.product.create

import com.meu.stock.model.Product

/**
 * Represents the UI state for the Product Form screen.
 */
data class ProductFormState(
    // Campos do formulário
    val name: String = "",
    val description: String = "",
    val stockQuantity: String = "",
    val lowStockQuantity: String = "",
    val priceUnit: String = "",
    val priceSale: String = "",
    val categoryId: Long? = null, // ID da categoria à qual o produto pertence


    // Controle da UI
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val deleteSuccess: Boolean = false,
    val errorMessage: String? = null,
    val isEditMode: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val productToDelete: Product? = null,
    val screenTitle: String = "Novo Produto"
)
