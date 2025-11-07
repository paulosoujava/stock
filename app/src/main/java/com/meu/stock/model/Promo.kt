package com.meu.stock.model

data class Promo(
    val id: Long = 0,
    val imageUri: String?,
    val title: String,
    val desc: String,
    val price: Double
)