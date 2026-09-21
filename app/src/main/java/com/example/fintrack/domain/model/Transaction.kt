package com.example.fintrack.domain.model

import java.time.Instant

data class Transaction(
    val id: Long = 0,
    val title: String,
    val amountMinor: Long,
    val type: TransactionType,
    val categoryId: Long,
    val paymentMethod: PaymentMethod,
    val note: String?,
    val occurredAt: Instant,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    init {
        require(title.isNotBlank()) { "Transaction title cannot be blank" }
        require(amountMinor > 0) { "Transaction amount must be greater than zero" }
        require(categoryId > 0) { "A valid category is required" }
    }
}

enum class TransactionType {
    INCOME,
    EXPENSE,
}

enum class PaymentMethod {
    CASH,
    BANK_ACCOUNT,
    DEBIT_CARD,
    CREDIT_CARD,
    MOBILE_PAYMENT,
    OTHER,
}
