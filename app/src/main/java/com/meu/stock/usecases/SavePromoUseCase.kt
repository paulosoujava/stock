package com.meu.stock.usecases

import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.mappers.toEntity
import com.meu.stock.model.Promo
import javax.inject.Inject


/**
 * Caso de Uso para salvar ou atualizar uma promoção.
 */
class SavePromoUseCase @Inject constructor(
    private val repository: IPromoRepository
) {
    suspend operator fun invoke(promo: Promo) {
        // Futuramente, você pode adicionar validações aqui.
        // if (promo.title.isBlank()) { throw InvalidTitleException() }
        repository.savePromo(promo.toEntity())
    }
}