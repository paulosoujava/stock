package com.meu.stock.bd.promo



import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoDao {    @Query("SELECT * FROM promocoes ORDER BY id DESC")
fun getAllPromos(): Flow<List<PromoEntity>>

    @Query("SELECT * FROM promocoes WHERE id = :promoId")
    fun getPromoById(promoId: Long): Flow<PromoEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(promo: PromoEntity)

    @Delete
    suspend fun delete(promo: PromoEntity)
}