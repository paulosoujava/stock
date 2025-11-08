package com.meu.stock.usecases.promo


import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.mappers.toPromo
import com.meu.stock.model.Promo
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de Uso para buscar uma promoção específica pelo seu ID.
 * Útil para a tela de edição ou detalhes.
 */
class GetPromoByIdUseCase @Inject constructor(
    private val repository: IPromoRepository
) {
    suspend operator fun invoke(id: Long): Promo? {
        return repository.getPromoById(id)
            .map { promoEntity ->
                promoEntity?.toPromo()
            }
            .firstOrNull()
    }
}
