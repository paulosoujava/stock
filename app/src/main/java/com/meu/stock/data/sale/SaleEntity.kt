package com.meu.stock.data.sale

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.meu.stock.data.month.MonthEntity
import java.util.Date

@Entity(
    tableName = "sales",
    foreignKeys = [ForeignKey(
        entity = MonthEntity::class,
        parentColumns = ["id"],
        childColumns = ["monthId"],
        onDelete = ForeignKey.CASCADE // Se um mês for deletado, suas vendas também serão
    )]
)
data class SaleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val monthId: Long,
    val clientId: Long,
    val clientName: String, // Desnormalização para facilitar a exibição
    val saleItemsJson: String, // JSON com a lista de produtos vendidos
    val totalAmount: Double,
    val timestamp: Date // Data e hora exatas da venda
)
