package com.meu.stock.model

/**
 * Representa um resumo das vendas para um determinado mês e ano.
 *
 * @property year O ano do resumo (ex: 2024).
 * @property month O nome do mês (ex: "Outubro").
 * @property totalSales O valor total vendido no mês.
 * @property salesCount A quantidade de vendas realizadas no mês.
 */
data class MonthlySummary(
    val year: Int,
    val month: String,
    val totalSales: Double,
    val salesCount: Int
)
