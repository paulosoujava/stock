package com.meu.stock.repositories


import com.meu.stock.contracts.ICategoryRepository
import com.meu.stock.data.category.CategoryDao
import com.meu.stock.mappers.toCategory
import com.meu.stock.mappers.toEntity
import com.meu.stock.model.Category
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.concurrent.atomics.update

class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao
) : ICategoryRepository {


    override suspend fun insert(category: Category) {
        categoryDao.insert(category.toEntity())
    }

    override suspend fun update(category: Category) {
        categoryDao.update(category.toEntity())
    }

    override fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { entities ->
            entities.map { it.toCategory() }
        }
    }
    override suspend fun deleteCategory(categoryId: Long) {
        categoryDao.deleteCategoryById(categoryId)
    }
    override fun getCategoryById(categoryId: Long): Flow<Category?> {
        return categoryDao.getCategoryById(categoryId).map { entity ->
            entity?.toCategory() // Reutiliza o mapeador
        }
    }
    override fun getCategoryCount(): Flow<Int> {
        return categoryDao.getCategoryCount()
    }
}


