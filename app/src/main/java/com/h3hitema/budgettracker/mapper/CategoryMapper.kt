package com.h3hitema.budgettracker.mapper

import com.h3hitema.budgettracker.data.local.CategoryEntity
import com.h3hitema.budgettracker.model.Category

fun CategoryEntity.toCategory(): Category {
    return Category(
        id = id,
        wording = wording
    )
}

fun Category.toEntity(): CategoryEntity {
    return CategoryEntity(
        id = id,
        wording = wording
    )
}

fun List<CategoryEntity>.toCategoryList(): List<Category> {
    return map { it.toCategory() }
}