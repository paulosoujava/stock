package com.meu.stock.usecases

import com.meu.stock.contracts.IProductRepository
import com.meu.stock.model.Product
import javax.inject.Inject

class SaveProductUseCase @Inject constructor(
    private val repository: IProductRepository
) {
    /**
     * Salva (insere ou atualiza) um produto.
     * @param product O produto a ser salvo. O ID nulo ou 0 indica uma inserção.
     * @throws IllegalArgumentException se o nome do produto for inválido.
     */
    suspend operator fun invoke(product: Product) {
        if (product.name.isBlank()) {
            throw IllegalArgumentException("O nome do produto não pode estar em branco.")
        }
        // A lógica de inserir vs. atualizar deve ser tratada no repositório.
        repository.saveProduct(product)
    }
}