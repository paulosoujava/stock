package com.meu.stock.views.ui.screens.products.category.create

/**
 * Estado para o formulário de criação/edição de categoria.
 */
data class CategoryFormState(
    val id: String? = null,
    val name: String = "",
    val description: String = "",
    val isLoading: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)