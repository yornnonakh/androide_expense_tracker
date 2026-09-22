package com.example.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    indices = [Index(value = ["type", "name"], unique = true)],
)
data class CategoryEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val iconKey: String,
    val type: TransactionTypeEntity,
    val createdAtEpochMillis: Long,
)
