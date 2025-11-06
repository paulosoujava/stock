package com.meu.stock.bd.month

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.meu.stock.bd.year.YearEntity

@Entity(
    tableName = "sale_months",
    // Chave estrangeira para garantir que o mês pertença a um ano válido
    foreignKeys = [ForeignKey(
        entity = YearEntity::class,
        parentColumns = ["id"],
        childColumns = ["yearId"],
        onDelete = ForeignKey.CASCADE // Se um ano for deletado, seus meses também serão
    )],
    // Garante que não teremos o mesmo mês duas vezes no mesmo ano
    indices = [Index(value = ["month", "yearId"], unique = true)]
)
data class MonthEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val month: Int, // Ex: 1 para Janeiro, 2 para Fevereiro
    val yearId: Long
)
