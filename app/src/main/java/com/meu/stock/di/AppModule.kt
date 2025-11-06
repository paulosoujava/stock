package com.meu.stock.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.meu.stock.contracts.IAuthRepository
import com.meu.stock.bd.AppDatabase
import com.meu.stock.bd.category.CategoryDao
import com.meu.stock.bd.client.ClientDao
import com.meu.stock.bd.note.NoteDao
import com.meu.stock.bd.product.ProductDao
import com.meu.stock.bd.sale.SaleHistoryDao
import com.meu.stock.repositories.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provê uma instância singleton do AppDatabase.
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "stock_database" // Nome do arquivo do banco de dados
        )
            // Em um app de produção, você implementaria uma migração real.
            // Em desenvolvimento, isso recria o banco se o schema mudar (útil, mas apaga os dados).
            .fallbackToDestructiveMigration(true)
            .build()
    }

    /**
     * Provê uma instância do categoryDao a partir do AppDatabase.
     */
    @Provides
    @Singleton
    fun provideCategoryDao(db: AppDatabase): CategoryDao {
        return db.categoryDao()    }


    /**
     * Provê uma instância do ClientDao a partir do AppDatabase.
     */
    @Provides
    @Singleton
    fun provideClientDao(appDatabase: AppDatabase): ClientDao {
        return appDatabase.clientDao()
    }

    /**
     * Provê uma instância do ProductDao a partir do AppDatabase.
     */
    @Singleton
    @Provides
    fun provideProductDao(database: AppDatabase): ProductDao {
        return database.productDao()
    }

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    fun provideSaleHistoryDao(appDatabase: AppDatabase): SaleHistoryDao {
        return appDatabase.saleHistoryDao()
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firestore: FirebaseFirestore
    ): IAuthRepository { // <-- O tipo de retorno DEVE ser a interface
        return AuthRepositoryImpl(firebaseAuth, firestore) // <-- O retorno DEVE ser a classe de implementação
    }

    @Provides
    @Singleton
    fun provideNoteDao(appDatabase: AppDatabase): NoteDao {
        return appDatabase.noteDao()
    }
}
