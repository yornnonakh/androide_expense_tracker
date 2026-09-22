package com.example.fintrack.core.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatMoney(amountMinor: Long, currencyCode: String = "USD"): String {
    val currency = Currency.getInstance(currencyCode)
    val divisor = powerOfTen(currency.defaultFractionDigits)
    return NumberFormat.getCurrencyInstance(Locale.getDefault()).apply {
        this.currency = currency
    }.format(amountMinor.toDouble() / divisor)
}

private fun powerOfTen(exponent: Int): Long =
    generateSequence(1L) { it * 10L }.drop(exponent.coerceAtLeast(0)).first()
