package com.meu.stock.views.ui.screens.products.category.list

import com.meu.stock.model.Category

/**
 * Estado para a tela de lista de categorias. */
data class CategoryListState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val error: String? = null,
    val categoryToDelete: Category? = null
)
