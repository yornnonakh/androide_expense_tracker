package com.example.fintrack.domain.usecase

import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject

data class TransactionInputErrors(
    val amount: String? = null,
    val title: String? = null,
    val category: String? = null,
) {
    val hasErrors: Boolean get() = amount != null || title != null || category != null
}

data class ValidatedTransactionInput(
    val amountMinor: Long,
    val title: String,
)

class ValidateTransactionInput @Inject constructor() {
    fun validate(
        amount: String,
        title: String,
        categoryId: Long?,
    ): Pair<ValidatedTransactionInput?, TransactionInputErrors> {
        val amountMinor = parseMinorUnits(amount)
        val errors = TransactionInputErrors(
            amount = when {
                amount.isBlank() -> "Enter an amount"
                amountMinor == null -> "Enter a valid amount with up to 2 decimal places"
                amountMinor <= 0 -> "Amount must be greater than zero"
                else -> null
            },
            title = if (title.isBlank()) "Enter a title" else null,
            category = if (categoryId == null) "Select a category" else null,
        )
        val validated = if (errors.hasErrors) null else {
            ValidatedTransactionInput(
                amountMinor = requireNotNull(amountMinor),
                title = title.trim(),
            )
        }
        return validated to errors
    }

    private fun parseMinorUnits(value: String): Long? = runCatching {
        BigDecimal(value.trim().replace(",", ""))
            .setScale(2, RoundingMode.UNNECESSARY)
            .movePointRight(2)
            .longValueExact()
    }.getOrNull()
}
