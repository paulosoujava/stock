package com.meu.stock.repositories

import java.util.Locale
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.meu.stock.contracts.IProductRepository
import com.meu.stock.contracts.ISaleHistoryRepository
import com.meu.stock.bd.month.MonthEntity
import com.meu.stock.bd.sale.RawSaleData
import com.meu.stock.bd.sale.SaleEntity
import com.meu.stock.bd.sale.SaleHistoryDao
import com.meu.stock.bd.year.YearEntity
import com.meu.stock.model.MonthlySummary
import com.meu.stock.model.SaleItem
import com.meu.stock.model.SalesMonth
import com.meu.stock.model.SalesYear
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import java.text.DateFormatSymbols
import java.util.Calendar
import javax.inject.Inject

class SaleHistoryRepositoryImpl @Inject constructor(
    private val saleHistoryDao: SaleHistoryDao,
    private val productRepository: IProductRepository,
    private val gson: Gson
): ISaleHistoryRepository {

    override suspend fun saveSale(
        clientId: Long,
        clientName: String,
        saleItems: List<SaleItem>,
        totalAmount: Double
    ) {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH é base 0

        // 1. Garante que o ano exista e obtém o ID
        var yearEntity = saleHistoryDao.getYear(currentYear)
        if (yearEntity == null) {
            val yearId = saleHistoryDao.insertYear(YearEntity(year = currentYear))
            yearEntity = YearEntity(id = yearId, year = currentYear)
        }

        // 2. Garante que o mês exista e obtém o ID
        var monthEntity = saleHistoryDao.getMonth(currentMonth, yearEntity.id)
        if (monthEntity == null) {
            val monthId = saleHistoryDao.insertMonth(MonthEntity(month = currentMonth, yearId = yearEntity.id))
            monthEntity = MonthEntity(id = monthId, month = currentMonth, yearId = yearEntity.id)
        }

        // 3. Prepara e salva a venda
        val saleItemsJson = gson.toJson(saleItems)
        val saleEntity = SaleEntity(
            monthId = monthEntity.id,
            clientId = clientId,
            clientName = clientName,
            saleItemsJson = saleItemsJson,
            totalAmount = totalAmount,
            timestamp = calendar.time
        )
        saleHistoryDao.insertSale(saleEntity)

        saleItems.forEach { saleItem ->
            val product = saleItem.product
            // Calcula a nova quantidade em estoque
            val newStockQuantity = product.stockQuantity - saleItem.quantity

            // Garante que o ID do produto não é nulo antes de chamar o repositório
            product.id?.let {
                productRepository.updateStockQuantity(
                    productId = it,
                    // Garante que o estoque não fique negativo no banco
                    newStockQuantity = newStockQuantity.coerceAtLeast(0)
                )
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getSalesHistory(): Flow<List<SalesYear>> {
        return saleHistoryDao.getAllYears().flatMapLatest { yearEntities ->
            if (yearEntities.isEmpty()) {
                flowOf(emptyList())
            } else {
                val yearFlows = yearEntities.map { yearEntity ->
                    // A chamada da função auxiliar permanece a mesma
                    getMonthsAndCalculateTotalsForYear(yearEntity.id, yearEntity.year)
                }
                combine(yearFlows) { yearsArray ->
                    // A lógica de combinação também permanece, pois o cálculo será feito internamente
                    yearsArray.toList()
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getCurrentMonthSummary(): Flow<MonthlySummary> {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonthInt = calendar.get(Calendar.MONTH) + 1
        val monthName = getMonthName(currentMonthInt)

        // Corrente reativa que observa o ano
        return saleHistoryDao.getYearFlow(currentYear).flatMapLatest { yearEntity ->
            if (yearEntity == null) {
                // Se o ano não existe, não há vendas. Retorna um fluxo com resumo zerado.
                flowOf(MonthlySummary(currentYear, monthName, 0.0, 0))
            } else {
                // Se o ano existe, observa o mês
                saleHistoryDao.getMonthFlow(currentMonthInt, yearEntity.id).flatMapLatest { monthEntity ->
                    if (monthEntity == null) {
                        // Se o mês não existe, não há vendas. Retorna resumo zerado.
                        flowOf(MonthlySummary(currentYear, monthName, 0.0, 0))
                    } else {
                        // Se o mês existe, observa as vendas e mapeia para o resumo
                        saleHistoryDao.getSalesForMonth(monthEntity.id).map { sales ->
                            val total = sales.sumOf { it.totalAmount } // Usa o totalAmount já salvo
                            val count = sales.size
                            MonthlySummary(currentYear, monthName, total, count)
                        }
                    }
                }
            }
        }
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getMonthsAndCalculateTotalsForYear(yearId: Long, yearValue: Int): Flow<SalesYear> {
        return saleHistoryDao.getMonthsForYear(yearId).flatMapLatest { monthEntities ->
            if (monthEntities.isEmpty()) {
                flowOf(SalesYear(year = yearValue, months = emptyList(), total = 0.0))
            } else {
                val monthFlows = monthEntities.map { monthEntity ->
                    saleHistoryDao.getSalesForMonth(monthEntity.id).map { sales ->
                        val monthlyTotal = calculateTotalFromSales(sales)
                        SalesMonth(
                            monthName = getMonthName(monthEntity.month),
                            monthNumber = monthEntity.month,
                            total = monthlyTotal
                        )
                    }
                }
                combine(monthFlows) { monthsArray ->
                    val yearTotal = monthsArray.sumOf { it.total } // Soma o total de cada mês
                    SalesYear(
                        year = yearValue,
                        months = monthsArray.toList(),
                        total = yearTotal // Passa o total calculado para o construtor
                    )
                }
            }
        }
    }


    /**
     * Calcula o faturamento total a partir de uma lista de vendas.
     * Esta função desserializa o JSON de cada venda e soma os valores.
     */
    private fun calculateTotalFromSales(sales: List<SaleEntity>): Double {
        var total = 0.0
        val saleItemType = object : TypeToken<List<SaleItem>>() {}.type

        sales.forEach { sale ->
            // Desserializa o JSON de volta para uma lista de SaleItem
            val items: List<SaleItem> = gson.fromJson(sale.saleItemsJson, saleItemType)
            // Soma o valor de cada item (preço * quantidade)
            val saleTotal = items.sumOf { it.product.priceSale * it.quantity }
            total += saleTotal
        }
        return total
    }

    /**
     * Converte o número do mês (1-12) para o nome completo em português.
     */
    private fun getMonthName(month: Int): String {
        // Garante que o índice esteja no intervalo válido (0-11)
        val monthIndex = (month - 1).coerceIn(0, 11)
        return DateFormatSymbols(Locale("pt", "BR")).months[monthIndex].replaceFirstChar { it.titlecase() }
    }


    override fun getRawSalesData(): Flow<List<RawSaleData>> {
        return saleHistoryDao.getRawSalesData()
    }
}