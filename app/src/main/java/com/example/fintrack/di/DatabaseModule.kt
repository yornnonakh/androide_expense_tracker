package com.example.fintrack.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.fintrack.data.local.dao.CategoryDao
import com.example.fintrack.data.local.dao.TransactionDao
import com.example.fintrack.data.local.database.DefaultCategories
import com.example.fintrack.data.local.database.FinTrackDatabase
import com.example.fintrack.data.local.database.Migration1To2
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideFinTrackDatabase(
        @ApplicationContext context: Context,
    ): FinTrackDatabase = Room.databaseBuilder(
        context,
        FinTrackDatabase::class.java,
        "fintrack.db",
    ).addMigrations(Migration1To2)
        .addCallback(
            object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    DefaultCategories.insert(db)
                }
            },
        )
        .build()

    @Provides
    fun provideTransactionDao(database: FinTrackDatabase): TransactionDao =
        database.transactionDao()

    @Provides
    fun provideCategoryDao(database: FinTrackDatabase): CategoryDao = database.categoryDao()
}
