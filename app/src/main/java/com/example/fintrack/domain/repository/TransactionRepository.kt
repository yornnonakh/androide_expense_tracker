package com.example.fintrack.domain.repository

import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import java.time.Instant
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(): Flow<List<Transaction>>

    fun observeById(id: Long): Flow<Transaction?>

    fun observeForPeriod(
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<List<Transaction>>

    fun observeRecent(limit: Int): Flow<List<Transaction>>

    fun observeTotalForPeriod(
        type: TransactionType,
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<Long>

    fun search(query: String): Flow<List<Transaction>>

    suspend fun save(transaction: Transaction): Long

    suspend fun deleteById(id: Long)
}
