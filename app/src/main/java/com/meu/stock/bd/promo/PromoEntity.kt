package com.meu.stock.bd.promo


import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Representa uma promoção no banco de dados local.
 *
 * @param id Chave primária autogerada.
 * @param imageUri URI da imagem da promoção (pode ser nula).
 * @param title O título da promoção.
 * @param desc A descrição detalhada.
 * @param price O preço da promoção.
 */
@Entity(tableName = "promocoes")
data class PromoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val imageUri: String?,
    val title: String,
    val desc: String,
    val price: Double
)