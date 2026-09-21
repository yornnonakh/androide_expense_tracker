package com.example.fintrack.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fintrack.data.local.converter.DatabaseConverters
import com.example.fintrack.data.local.dao.TransactionDao
import com.example.fintrack.data.local.dao.CategoryDao
import com.example.fintrack.data.local.entity.CategoryEntity
import com.example.fintrack.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, CategoryEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(DatabaseConverters::class)
abstract class FinTrackDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun categoryDao(): CategoryDao
}
