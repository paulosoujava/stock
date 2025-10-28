package com.meu.stock.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meu.stock.data.category.CategoryDao
import com.meu.stock.data.category.CategoryEntity
import com.meu.stock.data.client.ClientDao
import com.meu.stock.data.client.ClientEntity
import com.meu.stock.data.month.MonthEntity
import com.meu.stock.data.note.NoteDao
import com.meu.stock.data.note.NoteEntity
import com.meu.stock.data.product.ProductDao
import com.meu.stock.data.product.ProductEntity
import com.meu.stock.data.sale.SaleEntity
import com.meu.stock.data.sale.SaleHistoryDao
import com.meu.stock.data.year.YearEntity
import com.meu.stock.views.ui.utils.Converters

@Database(
    entities = [
        ClientEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        YearEntity::class,
        MonthEntity::class,
        SaleEntity::class,
        NoteEntity::class
    ],
    version = 12,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun saleHistoryDao(): SaleHistoryDao
    abstract fun noteDao(): NoteDao




}