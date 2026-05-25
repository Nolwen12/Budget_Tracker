package com.h3hitema.budgettracker.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithExpenses(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "category_id"
    )
    val expenses: List<ExpenseEntity>
)