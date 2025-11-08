package com.meu.stock.usecases.promo


import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.model.Promo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllPromosUseCase @Inject constructor(
    private val repository: IPromoRepository
) {
    operator fun invoke(): Flow<List<Promo>> {
        return repository.getAllPromos()
    }
}