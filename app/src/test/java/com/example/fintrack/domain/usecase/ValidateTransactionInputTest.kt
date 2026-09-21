package com.example.fintrack.domain.usecase

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class ValidateTransactionInputTest {
    private val validator = ValidateTransactionInput()

    @Test
    fun validDecimal_isConvertedToMinorUnitsExactly() {
        val (result, errors) = validator.validate("12.50", "Lunch", 1)

        assertEquals(1_250L, result?.amountMinor)
        assertEquals("Lunch", result?.title)
        assertEquals(false, errors.hasErrors)
    }

    @Test
    fun excessiveDecimalPlaces_areRejectedInsteadOfRounded() {
        val (result, errors) = validator.validate("12.345", "Lunch", 1)

        assertNull(result)
        assertNotNull(errors.amount)
    }

    @Test
    fun missingRequiredFields_returnInlineErrors() {
        val (result, errors) = validator.validate("0", "  ", null)

        assertNull(result)
        assertNotNull(errors.amount)
        assertNotNull(errors.title)
        assertNotNull(errors.category)
    }
}
