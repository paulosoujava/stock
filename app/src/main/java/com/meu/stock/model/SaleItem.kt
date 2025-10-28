package com.meu.stock.model


/**
 * Representa um item individual em uma venda.
 *
 * @property product O produto que está sendo vendido.
 * @property quantity A quantidade deste produto a ser vendida.
 */
data class SaleItem(
    val product: Product,
    var quantity: Int
)
