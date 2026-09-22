package com.example.fintrack.data.local.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.fintrack.data.local.database.FinTrackDatabase
import com.example.fintrack.data.local.entity.PaymentMethodEntity
import com.example.fintrack.data.local.entity.TransactionEntity
import com.example.fintrack.data.local.entity.TransactionTypeEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TransactionDaoTest {
    private lateinit var database: FinTrackDatabase
    private lateinit var dao: TransactionDao

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            FinTrackDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.transactionDao()
    }

    @After
    fun closeDatabase() {
        database.close()
    }

    @Test
    fun insertUpdateAndDelete_areObservedReactively() = runTest {
        val id = dao.insert(transaction(title = "Lunch", amountMinor = 850))

        assertEquals("Lunch", dao.observeById(id).first()?.title)

        val existing = requireNotNull(dao.observeById(id).first())
        assertEquals(1, dao.update(existing.copy(title = "Team lunch", amountMinor = 1_200)))
        assertEquals(1_200L, dao.observeById(id).first()?.amountMinor)

        assertEquals(1, dao.deleteById(id))
        assertNull(dao.observeById(id).first())
    }

    @Test
    fun periodAndTotals_useInclusiveStartAndExclusiveEnd() = runTest {
        val septemberStart = 1_788_192_000_000L
        val octoberStart = 1_790_784_000_000L

        dao.insert(
            transaction(
                title = "Opening salary",
                amountMinor = 250_000,
                type = TransactionTypeEntity.INCOME,
                occurredAt = septemberStart,
            ),
        )
        dao.insert(
            transaction(
                title = "Food",
                amountMinor = 65_000,
                occurredAt = septemberStart + 86_400_000,
            ),
        )
        dao.insert(
            transaction(
                title = "October expense",
                amountMinor = 10_000,
                occurredAt = octoberStart,
            ),
        )

        val september = dao.observeForPeriod(septemberStart, octoberStart).first()
        val income = dao.observeTotalForPeriod(
            TransactionTypeEntity.INCOME,
            septemberStart,
            octoberStart,
        ).first()
        val expenses = dao.observeTotalForPeriod(
            TransactionTypeEntity.EXPENSE,
            septemberStart,
            octoberStart,
        ).first()

        assertEquals(2, september.size)
        assertEquals(250_000L, income)
        assertEquals(65_000L, expenses)
    }

    @Test
    fun search_matchesTitleAndNoteIgnoringCase() = runTest {
        dao.insert(transaction(title = "Coffee", amountMinor = 450, note = "Client meeting"))
        dao.insert(transaction(title = "Bus fare", amountMinor = 200))

        assertEquals("Coffee", dao.search("coffee").first().single().title)
        assertEquals("Coffee", dao.search("CLIENT").first().single().title)
        assertEquals(emptyList<TransactionEntity>(), dao.search("rent").first())
    }

    private fun transaction(
        title: String,
        amountMinor: Long,
        type: TransactionTypeEntity = TransactionTypeEntity.EXPENSE,
        note: String? = null,
        occurredAt: Long = 1_788_192_000_000L,
    ) = TransactionEntity(
        title = title,
        amountMinor = amountMinor,
        type = type,
        categoryId = 1,
        paymentMethod = PaymentMethodEntity.CASH,
        note = note,
        occurredAtEpochMillis = occurredAt,
        createdAtEpochMillis = occurredAt,
        updatedAtEpochMillis = occurredAt,
    )
}
