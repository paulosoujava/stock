package com.meu.stock.contracts

import com.meu.stock.model.Category
import kotlinx.coroutines.flow.Flow

interface ICategoryRepository {
    suspend fun insert(category: Category)
    suspend fun update(category: Category)
    fun getAllCategories(): Flow<List<Category>>
    suspend fun deleteCategory(categoryId: Long)
    fun getCategoryById(categoryId: Long): Flow<Category?>
    fun getCategoryCount(): Flow<Int>
}
