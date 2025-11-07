package com.meu.stock.contracts
    import com.meu.stock.bd.promo.PromoEntity
    import com.meu.stock.model.Promo
    import kotlinx.coroutines.flow.Flow


    interface IPromoRepository {
        fun getAllPromos(): Flow<List<Promo>>
        fun getPromoById(id: Long): Flow<PromoEntity?>
        suspend fun savePromo(promo: PromoEntity)
        suspend fun deletePromo(promo: PromoEntity)
    }

