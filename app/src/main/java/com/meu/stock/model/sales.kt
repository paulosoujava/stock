package com.meu.stock.model


/**
 * Contém os dados de um balanço mensal, seja ele parcial (em aberto) ou final (fechado).
 */
data class MonthSummary(
    val salesCount: Int,
    val totalRevenue: Double,
    val liveSalesCount: Int,
    val localSalesCount: Int
)


// O restante dos modelos não precisa de alteração
data class SalesMonth(
    val monthName: String,
    val monthNumber: Int,
    val total: Double,
    var isExpanded: Boolean = false
)

data class SalesYear(
    val year: Int,
    val months: List<SalesMonth>,
    val total: Double = 0.0,
    var isExpanded: Boolean = false
)
