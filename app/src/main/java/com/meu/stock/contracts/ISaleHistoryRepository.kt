package com.meu.stock.contracts

import com.meu.stock.bd.sale.RawSaleData
import com.meu.stock.model.MonthlySummary
import com.meu.stock.model.SaleItem
import com.meu.stock.model.SalesYear
import kotlinx.coroutines.flow.Flow

/**
 * Interface que define as operações para o repositório de histórico de vendas.
 */
interface ISaleHistoryRepository {

    /**
     * Salva uma nova venda no banco de dados, cuidando da lógica
     * de criação de registros de ano e mês.
     *
     * @param clientId O ID do cliente da venda.
     * @param clientName O nome do cliente (desnormalizado para facilitar a consulta).
     * @param saleItems A lista de itens vendidos.
     * @param totalAmount O valor total da venda.
     */
    suspend fun saveSale(
        clientId: Long,
        clientName: String,
        saleItems: List<SaleItem>,
        totalAmount: Double
    )

    /**
     * Retorna um Flow com um resumo das vendas do mês corrente, incluindo
     * ano, nome do mês, total faturado e quantidade de vendas.
     */
    fun getCurrentMonthSummary(): Flow<MonthlySummary>

    /**
     * Retorna um fluxo com a lista completa do histórico de vendas,
     * aninhando anos, meses e o total de vendas de cada mês.
     */
    fun getSalesHistory(): Flow<List<SalesYear>>

    /**
     * Retorna um fluxo com uma lista de produtos e a quantidade total vendida de cada um,
     * ordenados do mais vendido para o menos vendido.
     */
    fun getRawSalesData(): Flow<List<RawSaleData>>

}