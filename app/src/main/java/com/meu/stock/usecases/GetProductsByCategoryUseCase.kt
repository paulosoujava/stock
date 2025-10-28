package com.meu.stock.usecases


import com.meu.stock.contracts.IProductRepository
import com.meu.stock.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProductsByCategoryUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    // Este UseCase recebe um 'categoryId' e retorna o Flow da lista de produtos.
    operator fun invoke(categoryId: Long): Flow<List<Product>> {
        return repository.getProductsByCategoryId(categoryId)
    }
}
