package com.meu.stock.usecases


import com.meu.stock.contracts.IProductRepository
import com.meu.stock.model.Product
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLowStockProductsUseCase @Inject constructor(
    private val productRepository: IProductRepository
) {
    operator fun invoke(): Flow<List<Product>> {
        return productRepository.getLowStockProducts()
    }
}
