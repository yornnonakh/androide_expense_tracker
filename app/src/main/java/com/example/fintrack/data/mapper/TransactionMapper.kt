package com.example.fintrack.data.mapper

import com.example.fintrack.data.local.entity.PaymentMethodEntity
import com.example.fintrack.data.local.entity.TransactionEntity
import com.example.fintrack.data.local.entity.TransactionTypeEntity
import com.example.fintrack.domain.model.PaymentMethod
import com.example.fintrack.domain.model.Transaction
import com.example.fintrack.domain.model.TransactionType
import java.time.Instant

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    title = title,
    amountMinor = amountMinor,
    type = type.toDomain(),
    categoryId = categoryId,
    paymentMethod = paymentMethod.toDomain(),
    note = note,
    occurredAt = Instant.ofEpochMilli(occurredAtEpochMillis),
    createdAt = Instant.ofEpochMilli(createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(updatedAtEpochMillis),
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    title = title.trim(),
    amountMinor = amountMinor,
    type = type.toEntity(),
    categoryId = categoryId,
    paymentMethod = paymentMethod.toEntity(),
    note = note?.trim()?.takeIf(String::isNotEmpty),
    occurredAtEpochMillis = occurredAt.toEpochMilli(),
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
)

fun TransactionTypeEntity.toDomain(): TransactionType =
    TransactionType.valueOf(name)

fun TransactionType.toEntity(): TransactionTypeEntity =
    TransactionTypeEntity.valueOf(name)

fun PaymentMethodEntity.toDomain(): PaymentMethod =
    PaymentMethod.valueOf(name)

fun PaymentMethod.toEntity(): PaymentMethodEntity =
    PaymentMethodEntity.valueOf(name)
