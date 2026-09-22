package com.example.fintrack.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fintrack.data.local.entity.TransactionEntity
import com.example.fintrack.data.local.entity.TransactionTypeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: TransactionEntity): Long

    @Update
    suspend fun update(transaction: TransactionEntity): Int

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteById(id: Long): Int

    @Query("SELECT * FROM transactions WHERE id = :id")
    fun observeById(id: Long): Flow<TransactionEntity?>

    @Query("SELECT * FROM transactions ORDER BY occurredAtEpochMillis DESC, id DESC")
    fun observeAll(): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        WHERE occurredAtEpochMillis >= :startInclusiveEpochMillis
          AND occurredAtEpochMillis < :endExclusiveEpochMillis
        ORDER BY occurredAtEpochMillis DESC, id DESC
        """,
    )
    fun observeForPeriod(
        startInclusiveEpochMillis: Long,
        endExclusiveEpochMillis: Long,
    ): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT * FROM transactions
        ORDER BY occurredAtEpochMillis DESC, id DESC
        LIMIT :limit
        """,
    )
    fun observeRecent(limit: Int): Flow<List<TransactionEntity>>

    @Query(
        """
        SELECT COALESCE(SUM(amountMinor), 0) FROM transactions
        WHERE type = :type
          AND occurredAtEpochMillis >= :startInclusiveEpochMillis
          AND occurredAtEpochMillis < :endExclusiveEpochMillis
        """,
    )
    fun observeTotalForPeriod(
        type: TransactionTypeEntity,
        startInclusiveEpochMillis: Long,
        endExclusiveEpochMillis: Long,
    ): Flow<Long>

    @Query(
        """
        SELECT * FROM transactions
        WHERE title LIKE '%' || :query || '%' COLLATE NOCASE
           OR COALESCE(note, '') LIKE '%' || :query || '%' COLLATE NOCASE
        ORDER BY occurredAtEpochMillis DESC, id DESC
        """,
    )
    fun search(query: String): Flow<List<TransactionEntity>>
}
