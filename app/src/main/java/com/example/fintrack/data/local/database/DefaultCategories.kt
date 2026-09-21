package com.example.fintrack.data.local.database

import androidx.sqlite.db.SupportSQLiteDatabase

object DefaultCategories {
    private val values = listOf(
        CategorySeed(1, "Food", "restaurant", "EXPENSE"),
        CategorySeed(2, "Transport", "directions_car", "EXPENSE"),
        CategorySeed(3, "Shopping", "shopping_bag", "EXPENSE"),
        CategorySeed(4, "Housing", "home", "EXPENSE"),
        CategorySeed(5, "Utilities", "bolt", "EXPENSE"),
        CategorySeed(6, "Health", "health", "EXPENSE"),
        CategorySeed(7, "Education", "school", "EXPENSE"),
        CategorySeed(8, "Entertainment", "movie", "EXPENSE"),
        CategorySeed(9, "Subscription", "subscriptions", "EXPENSE"),
        CategorySeed(10, "Travel", "flight", "EXPENSE"),
        CategorySeed(11, "Other", "category", "EXPENSE"),
        CategorySeed(12, "Salary", "payments", "INCOME"),
        CategorySeed(13, "Freelance", "work", "INCOME"),
        CategorySeed(14, "Business", "business", "INCOME"),
        CategorySeed(15, "Gift", "gift", "INCOME"),
        CategorySeed(16, "Investment", "trending_up", "INCOME"),
        CategorySeed(17, "Other Income", "category", "INCOME"),
    )

    fun insert(database: SupportSQLiteDatabase) {
        values.forEach { category ->
            database.execSQL(
                "INSERT OR IGNORE INTO categories " +
                    "(id, name, iconKey, type, createdAtEpochMillis) VALUES (?, ?, ?, ?, ?)",
                arrayOf<Any>(category.id, category.name, category.iconKey, category.type, 0L),
            )
        }
    }

    private data class CategorySeed(
        val id: Long,
        val name: String,
        val iconKey: String,
        val type: String,
    )
}
