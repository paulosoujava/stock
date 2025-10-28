package com.meu.stock.model

/**
 * Representa um produto de forma simplificada para listas.
 */
data class Product(
    val id: Long?,
    val name: String,
    val categoryName: String,
    val description: String,
    val stockQuantity: Int,
    val lowStockQuantity: Int,
    val categoryId: Long,
    val priceUnit: Double,
    val priceSale: Double,
)