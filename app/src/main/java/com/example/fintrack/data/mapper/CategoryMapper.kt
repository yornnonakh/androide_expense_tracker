package com.example.fintrack.data.mapper

import com.example.fintrack.data.local.entity.CategoryEntity
import com.example.fintrack.domain.model.Category
import com.example.fintrack.domain.model.TransactionType

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconKey = iconKey,
    type = TransactionType.valueOf(type.name),
)
