package com.example.fintrack.data.local.database

import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseMigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        FinTrackDatabase::class.java,
    )

    @Test
    fun migrate1To2_preservesTransactionsAndAddsDefaultCategories() {
        helper.createDatabase(TEST_DATABASE, 1).apply {
            execSQL(
                """
                INSERT INTO transactions (
                    id, title, amountMinor, type, categoryId, paymentMethod, note,
                    occurredAtEpochMillis, createdAtEpochMillis, updatedAtEpochMillis
                ) VALUES (1, 'Lunch', 850, 'EXPENSE', 1, 'CASH', NULL, 1, 1, 1)
                """.trimIndent(),
            )
            close()
        }

        helper.runMigrationsAndValidate(TEST_DATABASE, 2, true, Migration1To2).use { database ->
            database.query("SELECT COUNT(*) FROM transactions").use { cursor ->
                cursor.moveToFirst()
                assertEquals(1, cursor.getInt(0))
            }
            database.query("SELECT COUNT(*) FROM categories").use { cursor ->
                cursor.moveToFirst()
                assertEquals(17, cursor.getInt(0))
            }
        }
    }

    private companion object {
        const val TEST_DATABASE = "migration-test"
    }
}
