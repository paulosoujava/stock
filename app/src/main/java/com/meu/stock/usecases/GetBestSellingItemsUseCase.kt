package com.meu.stock.usecases

import com.meu.stock.contracts.IProductRepository
import com.meu.stock.contracts.ISaleHistoryRepository
import com.meu.stock.model.BestSellingProductInfo
import com.meu.stock.model.CustomerInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

/**
 * UseCase para obter a lista de produtos mais vendidos.
 * A lógica de cálculo é delegada ao repositório, que por sua vez usa
 * uma query otimizada do Room.
 */
class GetBestSellingItemsUseCase @Inject constructor(
    private val saleHistoryRepository: ISaleHistoryRepository,
    private val productRepository: IProductRepository // Precisamos para pegar os nomes dos produtos
) {
    operator fun invoke(): Flow<List<BestSellingProductInfo>> {
        val rawSalesFlow = saleHistoryRepository.getRawSalesData()
        val productsFlow = productRepository.getAllProducts() // Pega todos os ProductEntity

        return combine(rawSalesFlow, productsFlow) { rawSales, products ->
            // Cria um mapa para acesso rápido aos nomes dos produtos pelo ID
            val productMap = products.associateBy { it.id }

            // 1. Agrupa os dados brutos por productId
            val salesByProduct = rawSales.groupBy { it.productId }

            // 2. Transforma os dados agrupados em nosso modelo final
            salesByProduct.mapNotNull { (productId, salesData) ->
                val product = productMap[productId]
                // Só continua se o produto ainda existir na tabela de produtos
                product?.let {
                    // Cria uma lista de clientes únicos para este produto
                    val customers = salesData
                        .map { CustomerInfo(it.customerId, it.customerName) }
                        .distinct()

                    BestSellingProductInfo(
                        productId = productId,
                        productName = it.name,
                        totalSalesCount = salesData.size, // O total de vendas é o tamanho da lista
                        customers = customers
                    )
                }
            }
                // 3. Ordena a lista final pelo número de vendas
                .sortedByDescending { it.totalSalesCount }
        }
    }
}