package com.example.fintrack.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [
        Index(value = ["occurredAtEpochMillis"]),
        Index(value = ["categoryId"]),
        Index(value = ["type"]),
        Index(value = ["paymentMethod"]),
    ],
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amountMinor: Long,
    val type: TransactionTypeEntity,
    val categoryId: Long,
    val paymentMethod: PaymentMethodEntity,
    val note: String?,
    val occurredAtEpochMillis: Long,
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

enum class TransactionTypeEntity {
    INCOME,
    EXPENSE,
}

enum class PaymentMethodEntity {
    CASH,
    BANK_ACCOUNT,
    DEBIT_CARD,
    CREDIT_CARD,
    MOBILE_PAYMENT,
    OTHER,
}
