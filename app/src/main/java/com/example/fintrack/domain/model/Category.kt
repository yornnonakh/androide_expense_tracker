package com.example.fintrack.domain.model

data class Category(
    val id: Long,
    val name: String,
    val iconKey: String,
    val type: TransactionType,
)
