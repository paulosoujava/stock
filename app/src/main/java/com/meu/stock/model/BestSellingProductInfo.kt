package com.meu.stock.model

/**
* Modelo de dados enriquecido para a tela de "Mais Vendidos".
* Mostra o produto, o número total de vendas em que ele apareceu
* e uma lista dos clientes que o compraram.
*/
data class BestSellingProductInfo(
    val productId: Long,
    val productName: String,
    val totalSalesCount: Int, // Número de vendas (carrinhos) em que o produto apareceu
    val customers: List<CustomerInfo> // Lista de clientes que compraram
)

data class CustomerInfo(
    val customerId: Long,
    val customerName: String
)