package com.meu.stock.di



import com.meu.stock.contracts.IAlarmScheduler
import com.meu.stock.contracts.ICategoryRepository
import com.meu.stock.contracts.IClientRepository
import com.meu.stock.contracts.IGetClientsUseCase
import com.meu.stock.contracts.IGetTotalClientsUseCase
import com.meu.stock.contracts.INoteRepository
import com.meu.stock.contracts.IProductRepository
import com.meu.stock.contracts.IPromoRepository
import com.meu.stock.contracts.ISaleHistoryRepository
import com.meu.stock.repositories.AlarmSchedulerImpl
import com.meu.stock.repositories.CategoryRepositoryImpl
import com.meu.stock.repositories.ClientRepositoryImpl
import com.meu.stock.repositories.NoteRepositoryImpl
import com.meu.stock.repositories.ProductRepositoryImpl
import com.meu.stock.repositories.PromoRepositoryImpl
import com.meu.stock.repositories.SaleHistoryRepositoryImpl
import com.meu.stock.usecases.GetClientsUseCase
import com.meu.stock.usecases.GetTotalClientsUseCase


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfacesModule {

    @Binds
    @Singleton
    abstract fun bindPromoRepository(
        promoRepositoryImpl: PromoRepositoryImpl
    ): IPromoRepository


    @Binds
    @Singleton
    abstract fun bindClientRepository(
        clientRepository: ClientRepositoryImpl
    ): IClientRepository

    @Binds
    @Singleton
    abstract fun bindGetClientsUseCase(
        getClientsUseCase: GetClientsUseCase
    ): IGetClientsUseCase

    @Binds
    @Singleton
    abstract fun bindGetTotalClientsUseCase(
        getTotalClientsUseCase: GetTotalClientsUseCase
    ): IGetTotalClientsUseCase

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        categoryRepositoryImpl: CategoryRepositoryImpl
    ): ICategoryRepository

    @Singleton
    @Binds
    abstract fun bindProductRepository(
        productRepositoryImpl: ProductRepositoryImpl
    ): IProductRepository

    @Binds
    @Singleton
    abstract fun bindSaleHistoryRepository(
        saleHistoryRepositoryImpl: SaleHistoryRepositoryImpl
    ): ISaleHistoryRepository


    @Binds
    @Singleton
    abstract fun bindNoteRepository(
        noteRepositoryImpl: NoteRepositoryImpl
    ): INoteRepository

    @Binds
    @Singleton
    abstract fun bindAlarmScheduler(
        alarmSchedulerImpl: AlarmSchedulerImpl
    ): IAlarmScheduler
}
