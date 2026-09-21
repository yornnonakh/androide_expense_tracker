package com.example.fintrack.data.local.converter

import androidx.room.TypeConverter
import com.example.fintrack.data.local.entity.PaymentMethodEntity
import com.example.fintrack.data.local.entity.TransactionTypeEntity

class DatabaseConverters {
    @TypeConverter
    fun transactionTypeToString(value: TransactionTypeEntity): String = value.name

    @TypeConverter
    fun stringToTransactionType(value: String): TransactionTypeEntity =
        enumValueOf<TransactionTypeEntity>(value)

    @TypeConverter
    fun paymentMethodToString(value: PaymentMethodEntity): String = value.name

    @TypeConverter
    fun stringToPaymentMethod(value: String): PaymentMethodEntity =
        enumValueOf<PaymentMethodEntity>(value)
}
