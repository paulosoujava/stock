package com.meu.stock.usecases.product

import com.meu.stock.contracts.IProductRepository
import com.meu.stock.model.Product
import javax.inject.Inject

class GetProductByIdUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    /**
     * Busca um produto específico pelo seu ID.
     * @param id O ID do produto a ser buscado.
     * @return O objeto [Product] se encontrado, ou null se não existir.
     */
    suspend operator fun invoke(id: Long): Product? {
        return repository.getProductById(id)
    }
}