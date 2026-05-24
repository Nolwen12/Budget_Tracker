package com.h3hitema.budgettracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository

class ExpenseViewModelFactory(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ExpenseViewModel(expenseRepository, categoryRepository) as T
    }
}