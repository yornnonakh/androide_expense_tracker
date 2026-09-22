package com.example.fintrack.data.local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val Migration1To2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS categories (
                id INTEGER NOT NULL PRIMARY KEY,
                name TEXT NOT NULL,
                iconKey TEXT NOT NULL,
                type TEXT NOT NULL,
                createdAtEpochMillis INTEGER NOT NULL
            )
            """.trimIndent(),
        )
        db.execSQL(
            "CREATE UNIQUE INDEX IF NOT EXISTS index_categories_type_name " +
                "ON categories (type, name)",
        )
        DefaultCategories.insert(db)
    }
}
