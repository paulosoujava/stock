package com.meu.stock.usecases.product


import com.meu.stock.contracts.IProductRepository
import javax.inject.Inject

class DeleteProductUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    /**
     * Deleta um produto pelo seu ID.
     * @param id O ID do produto a ser deletado.
     */
    suspend operator fun invoke(id: Long) {
        repository.deleteProduct(id)
    }
}