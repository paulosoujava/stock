package com.meu.stock.usecases

import com.meu.stock.contracts.ICategoryRepository
import com.meu.stock.model.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryByIdUseCase @Inject constructor(
    private val repository: ICategoryRepository
) {
    operator fun invoke(categoryId: Long): Flow<Category?> {
        return repository.getCategoryById(categoryId)}
}
