package com.meu.stock.usecases.product


import com.meu.stock.contracts.IProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductCountUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    operator fun invoke(): Flow<Int> {
        return repository.getProductCount()
    }
}