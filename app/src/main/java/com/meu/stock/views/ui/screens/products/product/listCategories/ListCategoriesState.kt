package com.meu.stock.views.ui.screens.products.product.listCategories

import com.meu.stock.model.Category
import com.meu.stock.model.Product

data class ListCategoriesState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val productCount: Int = 0
)

