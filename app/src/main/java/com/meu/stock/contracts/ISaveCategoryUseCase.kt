package com.meu.stock.contracts

import com.meu.stock.model.Category

interface ISaveCategoryUseCase {
    suspend operator fun invoke(category: Category)
}