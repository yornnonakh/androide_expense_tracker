package com.example.fintrack.di

import com.example.fintrack.data.repository.OfflineTransactionRepository
import com.example.fintrack.data.repository.OfflineCategoryRepository
import com.example.fintrack.domain.repository.CategoryRepository
import com.example.fintrack.domain.repository.TransactionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindTransactionRepository(
        repository: OfflineTransactionRepository,
    ): TransactionRepository

    @Binds
    @Singleton
    abstract fun bindCategoryRepository(
        repository: OfflineCategoryRepository,
    ): CategoryRepository
}
