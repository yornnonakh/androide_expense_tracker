package com.example.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.example.fintrack.data.local.entity.CategoryEntity
import com.example.fintrack.data.local.entity.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY type, name")
    fun observeAll(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE type = :type ORDER BY name")
    fun observeByType(type: TransactionTypeEntity): Flow<List<CategoryEntity>>
}
