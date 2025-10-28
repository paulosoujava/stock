package com.meu.stock.data.year

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sale_years",
    // Garante que não teremos anos duplicados
    indices = [Index(value = ["year"], unique = true)]
)
data class YearEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val year: Int // Ex: 2024
)
