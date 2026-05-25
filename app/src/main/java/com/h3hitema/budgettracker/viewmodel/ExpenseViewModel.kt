package com.h3hitema.budgettracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.data.local.ExpenseEntity
import com.h3hitema.budgettracker.model.Expense
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import androidx.lifecycle.map
import com.h3hitema.budgettracker.mapper.toEntity
import com.h3hitema.budgettracker.mapper.toExpense

class ExpenseViewModel(
    private val expenseRepository: ExpenseRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            categoryRepository.insertInitialCategoryIfNeeded()
            Log.d(TAG, "Catégories initialisées")

            // Vérification
            val count = categoryRepository.count()
            Log.d(TAG, "Nombre de catégories en base : $count")
        }
    }

    companion object {
        private const val TAG = "BudgetTracker"
    }

    // ── LiveData des dépenses avec categoryName rempli ──────────
    val expenses: LiveData<List<Expense>> = categoryRepository.getCategoriesWithExpenses().map { categoriesWithExpenses ->
        Log.d(TAG, "Catégories reçues : ${categoriesWithExpenses.size}")
        categoriesWithExpenses.flatMap { categoryWithExpenses ->
            Log.d(TAG, "Catégorie : ${categoryWithExpenses.category.wording}, expenses : ${categoryWithExpenses.expenses.size}")
            categoryWithExpenses.expenses.map { entity ->
                entity.toExpense(categoryName = categoryWithExpenses.category.wording)
            }
        }
    }

    // ── Chargement ───────────────────────────────────────────────
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    // ── Sélections utilisateur ───────────────────────────────────
    var selectedCategoryId: Long = 0L
    var selectedDate: Date = Date()

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        selectedDate = calendar.time
    }

    // ── CRUD ─────────────────────────────────────────────────────
    fun addExpense(title: String, amountStr: String, note: String) {
        val cleanedTitle = title.trim()
        val cleanedAmount = amountStr.trim().toDoubleOrNull()
        val cleanedNote = note.trim()

        if (cleanedTitle.isEmpty()) {
            Log.d(TAG, "Ajout refusé : titre vide")
            return
        }

        if (cleanedAmount == null || cleanedAmount <= 0.0) {
            Log.d(TAG, "Ajout refusé : montant invalide")
            return
        }

        viewModelScope.launch {
            val createdExpense = expenseRepository.addExpense(
                title = cleanedTitle,
                amount = cleanedAmount,
                date = selectedDate as java.util.Date,
                note = cleanedNote,
                categoryId = selectedCategoryId
            )
            Log.d(TAG, "Dépense ajoutée : $createdExpense")
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            val deleted = expenseRepository.deleteExpense(expense.toEntity())

            if (deleted) {
                Log.d(TAG, "Dépense supprimée : $expense")
            } else {
                Log.d(TAG, "Suppression impossible : dépense introuvable")
            }
        }
    }

    fun clearAllExpense() {
        viewModelScope.launch {
            expenseRepository.clearAll()
            Log.d(TAG, "Toutes les dépenses supprimées")
        }
    }
}