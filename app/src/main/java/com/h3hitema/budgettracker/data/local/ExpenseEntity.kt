package com.h3hitema.budgettracker.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date
import java.util.Locale

@Entity(tableName = "expense")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int =0,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "amount")
    val amount: Double,

    @ColumnInfo(name = "category")
    val category: Locale.Category,

    @ColumnInfo(name = "date")
    val date: Date,

    @ColumnInfo(name = "note")
    val note: String
)