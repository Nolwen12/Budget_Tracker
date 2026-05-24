package com.h3hitema.budgettracker.mapper

import com.h3hitema.budgettracker.data.local.ExpenseEntity
import com.h3hitema.budgettracker.model.Expense

fun ExpenseEntity.toExpense(categoryName: String = ""): Expense {
    return Expense(
        id = id,
        title = title,
        amount = amount,
        date = date,
        note = note,
        categoryId = categoryId,
        categoryName = categoryName
    )
}

fun Expense.toEntity(): ExpenseEntity {
    return ExpenseEntity(
        id = id,
        title = title,
        amount = amount,
        date = date,
        note = note,
        categoryId = categoryId
    )
}

fun List<ExpenseEntity>.toExpenseList(): List<Expense> {
    return map { it.toExpense() }
}