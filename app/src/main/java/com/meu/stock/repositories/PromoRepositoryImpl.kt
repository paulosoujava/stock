package com.meu.stock.repositories

import com.meu.stock.bd.promo.PromoDao
import com.meu.stock.bd.promo.PromoEntity
import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.mappers.toEntity
import com.meu.stock.mappers.toPromo
import com.meu.stock.model.Promo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PromoRepositoryImpl @Inject constructor(
    private val promoDao: PromoDao
) : IPromoRepository {

    override fun getAllPromos(): Flow<List<Promo>> {
        return promoDao.getAllPromos().map { entities ->
            entities.map { it.toPromo() } // Usando a função de extensão
        }
    }

    override fun getPromoById(id: Long): Flow<PromoEntity?> {
        return promoDao.getPromoById(id).map { entity ->
            entity
        }
    }

    override suspend fun savePromo(promo: PromoEntity) {
        promoDao.save(promo)
    }

    override suspend fun deletePromo(promo: PromoEntity) {
        promoDao.delete(promo)
    }
}