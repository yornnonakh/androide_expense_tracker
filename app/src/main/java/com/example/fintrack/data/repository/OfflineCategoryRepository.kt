package com.example.fintrack.data.repository

import com.example.fintrack.data.local.dao.CategoryDao
import com.example.fintrack.data.mapper.toDomain
import com.example.fintrack.data.mapper.toEntity
import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.CategoryRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineCategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeAll(): Flow<List<Category>> =
        categoryDao.observeAll().map { items -> items.map { it.toDomain() } }

    override fun observeByType(type: TransactionType): Flow<List<Category>> =
        categoryDao.observeByType(type.toEntity())
            .map { items -> items.map { it.toDomain() } }
}
