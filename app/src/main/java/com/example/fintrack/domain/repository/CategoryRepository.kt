package com.example.fintrack.domain.repository

import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun observeAll(): Flow<List<Category>>
    fun observeByType(type: TransactionType): Flow<List<Category>>
}
