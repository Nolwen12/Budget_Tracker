package com.h3hitema.budgettracker.data.local

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface ExpenseDao{
    @Query("SELECT * FROM expense ORDER BY id DESC")
    suspend fun getAllExpense(): List<ExpenseEntity>

    @Query("SELECT * FROM expense WHERE id = :expenseId LIMIT 1")
    suspend fun getExpenseById(expenseId: Int): ExpenseEntity?

    @Insert
    suspend fun insertExpense(expense: ExpenseEntity): Long

    @Update
    suspend fun updateExpense(expense: ExpenseEntity)

    @Delete
    suspend fun deleteExpense(expense: ExpenseEntity)

    @Query("DELETE FROM expense WHERE id = :expenseId")
    suspend fun deleteExpenseId(expenseId: Int)

    @Query("DELETE FROM expense")
    suspend fun deleteAllExpense()

    @Query("SELECT COUNT(*) FROM expense")
    suspend fun countExpense(): Int

    @Query("SELECT * FROM expense ORDER BY id DESC")
    fun getAllExpenseLiveData(): LiveData<List<ExpenseEntity>>
}