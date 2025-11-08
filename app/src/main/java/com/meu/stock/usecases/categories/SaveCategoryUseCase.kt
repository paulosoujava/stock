package com.meu.stock.usecases.categories


import com.meu.stock.contracts.ICategoryRepository
import com.meu.stock.contracts.ISaveCategoryUseCase
import com.meu.stock.model.Category

import javax.inject.Inject

class SaveCategoryUseCase @Inject constructor(
    private val repository: ICategoryRepository
): ISaveCategoryUseCase {
    override suspend operator fun invoke(category: Category) {
        if (category.name.isBlank()) {
            throw IllegalArgumentException("O nome da categoria não pode ser vazio.")
        }
        // Lógica principal de decisão
        if (category.id == null) {
            // Se o ID é nulo, é uma nova categoria.  INSERIR.
            repository.insert(category)
        } else {
            // Se o ID não é nulo, é uma categoria existente.  ATUALIZAR.
            repository.update(category)
        }
    }
}
