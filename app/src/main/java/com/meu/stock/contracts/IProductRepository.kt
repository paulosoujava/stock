package com.meu.stock.contracts

import com.meu.stock.model.Product
import kotlinx.coroutines.flow.Flow

interface IProductRepository {
    fun getProductsByCategoryId(categoryId: Long): Flow<List<Product>>
    fun getProductCount(): Flow<Int>
    suspend fun saveProduct(product: Product)
    suspend fun getProductById(id: Long): Product?
    suspend fun deleteProduct(id: Long)
    suspend fun searchProducts(query: String): List<Product>
    fun getLowStockProducts(): Flow<List<Product>>
    suspend fun updateStockQuantity(productId: Long, newStockQuantity: Int)
    fun getAllProducts(): Flow<List<Product>>


}
