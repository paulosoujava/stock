package com.meu.stock.views.ui.screens.products.product.listProductsByCategory

import com.meu.stock.model.Product

data class ListProductsByCategoryState(
    val products: List<Product> = emptyList(),
    val categoryName: String = "Produtos", // Valor padrão
    val categoryId: Long = -1L,
    val isLoading: Boolean = true,
    val isSearchActive: Boolean = false, // Controla se a barra de pesquisa está visível
    val searchQuery: String = "", // Armazena o texto da pesquisa
    val filteredProducts: List<Product> = emptyList(),
)
