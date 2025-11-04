package com.meu.stock.data.sale

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.meu.stock.data.month.MonthEntity
import com.meu.stock.data.year.YearEntity
import kotlinx.coroutines.flow.Flow


// temporário para o resultado da query
data class RawSaleData(
    val productId: Long,
    val customerId: Long,
    val customerName: String
)

@Dao
interface SaleHistoryDao {
    @Query("SELECT * FROM sale_years WHERE year = :year")
    suspend fun getYear(year: Int): YearEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertYear(year: YearEntity): Long


    @Query("SELECT * FROM sale_months WHERE month = :month AND yearId = :yearId")
    suspend fun getMonth(month: Int, yearId: Long): MonthEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMonth(month: MonthEntity): Long


    @Insert
    suspend fun insertSale(sale: SaleEntity)

    /**
     * Calcula a SOMA total de todas as vendas (`totalAmount`) para um ID de mês.
     */
    @Query("SELECT SUM(totalAmount) FROM sales WHERE monthId = :monthId")
    fun getTotalSalesForMonth(monthId: Long): Flow<Double?>

    /**
     * CONTA o número de vendas para um determinado ID de mês.
     */
    @Query("SELECT COUNT(id) FROM sales WHERE monthId = :monthId")
    fun getSalesCountForMonth(monthId: Long): Flow<Int>

    /**
     * Retorna uma lista de todos os anos que têm registros de vendas,
     * em ordem decrescente (o mais recente primeiro).
     */
    @Query("SELECT * FROM sale_years ORDER BY year DESC")
    fun getAllYears(): Flow<List<YearEntity>>

    /**
     * Retorna uma lista de todos os meses para um ID de ano específico,
     * em ordem decrescente (o mais recente primeiro).
     */
    @Query("SELECT * FROM sale_months WHERE yearId = :yearId ORDER BY month DESC")
    fun getMonthsForYear(yearId: Long): Flow<List<MonthEntity>>

    /**
     * Retorna uma lista de todas as vendas para um ID de mês específico,
     * em ordem decrescente (a mais recente primeiro).
     */
    @Query("SELECT * FROM sales WHERE monthId = :monthId ORDER BY timestamp DESC")
    fun getSalesForMonth(monthId: Long): Flow<List<SaleEntity>>

    /**
     * Usa o caminho JSON correto ('$.product.id') para extrair o ID do produto
     * que está aninhado dentro do objeto 'product'.
     */
    @Query("""
        SELECT
            CAST(json_extract(j.value, '$.product.id') AS INTEGER) as productId, -- CORREÇÃO PRINCIPAL
            s.clientId as customerId,
            s.clientName as customerName
        FROM
            sales AS s,
            json_each(s.saleItemsJson) AS j
        WHERE productId IS NOT NULL
    """)
    fun getRawSalesData(): Flow<List<RawSaleData>>

    // Funções reativas para observar mudanças
    @Query("SELECT * FROM sale_years WHERE year = :year")
    fun getYearFlow(year: Int): Flow<YearEntity?>

    @Query("SELECT * FROM sale_months WHERE month = :month AND yearId = :yearId")
    fun getMonthFlow(month: Int, yearId: Long): Flow<MonthEntity?>

    // TEMPORÁRIO ao seu SaleHistoryDao.kt
    @Query("SELECT saleItemsJson FROM sales WHERE saleItemsJson IS NOT NULL AND saleItemsJson != ''")
    fun debugGetRawJsonStrings(): List<String>
}