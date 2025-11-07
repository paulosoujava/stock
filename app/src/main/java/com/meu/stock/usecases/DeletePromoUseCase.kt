package com.meu.stock.usecases


import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.mappers.toEntity
import com.meu.stock.model.Promo
import javax.inject.Inject

class DeletePromoUseCase @Inject constructor(
    private val repository: IPromoRepository
) {
    suspend operator fun invoke(promo: Promo) {
        repository.deletePromo(promo.toEntity())
    }
}