package com.h3hitema.budgettracker.model

import java.util.Date

data class Expense(
    val id: Int,
    val title: String,
    val amount: Double,
    val date: Date,
    val note: String,
    val categoryId: Long,
    val categoryName: String = ""
)