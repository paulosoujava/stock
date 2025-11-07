package com.meu.stock.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.meu.stock.bd.category.CategoryDao
import com.meu.stock.bd.category.CategoryEntity
import com.meu.stock.bd.client.ClientDao
import com.meu.stock.bd.client.ClientEntity
import com.meu.stock.bd.month.MonthEntity
import com.meu.stock.bd.note.NoteDao
import com.meu.stock.bd.note.NoteEntity
import com.meu.stock.bd.product.ProductDao
import com.meu.stock.bd.product.ProductEntity
import com.meu.stock.bd.promo.PromoDao
import com.meu.stock.bd.promo.PromoEntity
import com.meu.stock.bd.sale.SaleEntity
import com.meu.stock.bd.sale.SaleHistoryDao
import com.meu.stock.bd.year.YearEntity
import com.meu.stock.views.ui.utils.Converters

@Database(
    entities = [
        ClientEntity::class,
        CategoryEntity::class,
        ProductEntity::class,
        YearEntity::class,
        MonthEntity::class,
        SaleEntity::class,
        NoteEntity::class,
        PromoEntity::class
    ],
    version = 14,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun saleHistoryDao(): SaleHistoryDao
    abstract fun noteDao(): NoteDao
    abstract fun promoDao(): PromoDao

}