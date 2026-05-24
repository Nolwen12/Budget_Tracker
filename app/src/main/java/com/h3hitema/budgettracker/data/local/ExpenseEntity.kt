package com.h3hitema.budgettracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "date")
    val date: java.util.Date,

    @ColumnInfo(name = "note")
    val note: String,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,
)