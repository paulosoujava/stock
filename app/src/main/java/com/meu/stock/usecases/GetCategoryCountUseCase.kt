package com.meu.stock.usecases


import com.meu.stock.contracts.ICategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCategoryCountUseCase @Inject constructor(
    private val repository: ICategoryRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getCategoryCount()
    }
}
