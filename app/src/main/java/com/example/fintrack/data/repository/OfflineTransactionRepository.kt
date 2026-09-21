package com.example.fintrack.data.repository

import com.example.fintrack.data.local.dao.TransactionDao
import com.example.fintrack.data.mapper.toDomain
import com.example.fintrack.data.mapper.toEntity
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import com.example.fintrack.domain.repository.TransactionRepository
import java.time.Instant
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineTransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
) : TransactionRepository {
    override fun observeAll(): Flow<List<Transaction>> =
        transactionDao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeById(id: Long): Flow<Transaction?> =
        transactionDao.observeById(id).map { it?.toDomain() }

    override fun observeForPeriod(
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<List<Transaction>> {
        require(startInclusive <= endExclusive) { "Period end must not be before its start" }
        return transactionDao.observeForPeriod(
            startInclusiveEpochMillis = startInclusive.toEpochMilli(),
            endExclusiveEpochMillis = endExclusive.toEpochMilli(),
        ).map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeRecent(limit: Int): Flow<List<Transaction>> {
        require(limit >= 0) { "Recent transaction limit must be non-negative" }
        return transactionDao.observeRecent(limit).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun observeTotalForPeriod(
        type: TransactionType,
        startInclusive: Instant,
        endExclusive: Instant,
    ): Flow<Long> {
        require(startInclusive <= endExclusive) { "Period end must not be before its start" }
        return transactionDao.observeTotalForPeriod(
            type = type.toEntity(),
            startInclusiveEpochMillis = startInclusive.toEpochMilli(),
            endExclusiveEpochMillis = endExclusive.toEpochMilli(),
        )
    }

    override fun search(query: String): Flow<List<Transaction>> =
        transactionDao.search(query.trim()).map { entities -> entities.map { it.toDomain() } }

    override suspend fun save(transaction: Transaction): Long {
        val entity = transaction.toEntity()
        return if (transaction.id == 0L) {
            transactionDao.insert(entity)
        } else {
            transactionDao.update(entity)
            transaction.id
        }
    }

    override suspend fun deleteById(id: Long) {
        if (id > 0) {
            transactionDao.deleteById(id)
        }
    }
}
