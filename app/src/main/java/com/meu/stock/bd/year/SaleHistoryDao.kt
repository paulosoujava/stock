package com.meu.stock.bd.year

import androidx.room.Dao
import androidx.room.Query
import com.meu.stock.bd.month.MonthEntity
import com.meu.stock.bd.sale.SaleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SaleHistoryDao {

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
}