package com.meu.stock.bd.product

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.meu.stock.bd.category.CategoryEntity

@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT // Alterado para RESTRICT para mais segurança
        )
    ]
)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = 0, // 1. O ID agora é Long, com valor padrão 0 para inserções

    val name: String,
    val description: String?, // 2. Mantido como nulável, pois descrições podem ser opcionais

    @ColumnInfo(name = "stock_quantity")
    val stockQuantity: Int,

    @ColumnInfo(name = "low_stock_quantity")
    val lowStockQuantity: Int,

    // Chave estrangeira que conecta este produto a uma categoria
    @ColumnInfo(name = "category_id", index = true)
    val categoryId: Long,

    @ColumnInfo(name = "price_unit")
    val priceUnit: Double,

    @ColumnInfo(name = "price_sale")
    val priceSale: Double

)