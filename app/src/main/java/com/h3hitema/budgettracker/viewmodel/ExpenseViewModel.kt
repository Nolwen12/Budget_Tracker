package com.h3hitema.budgettracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.data.local.ExpenseEntity
import com.h3hitema.budgettracker.model.Expense
import com.h3hitema.budgettracker.repository.ExpenseRepository
import kotlinx.coroutines.launch
import java.util.Date

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        private const val TAG = "BudgetTracker"
    }

    private val repository: ExpenseRepository

    // LiveData observée par l'Activity : chaque nouvelle valeur déclenche un rendu UI.
    private val _expense = MutableLiveData<List<ExpenseEntity>>(emptyList())
    val expense: LiveData<List<ExpenseEntity>> = _expense

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    var selectedCategoryId: Long = 0L
    var selectedDate: Date = Date()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = ExpenseRepository(database.expenseDao())

        // L'initialisation reste dans le ViewModel pour ne pas charger l'Activity.
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            _isLoading.value = true
            
            loadExpense()

            // Quand isLoading repasse à false, l'écran peut masquer le chargement.
            _isLoading.value = false

            Log.d(TAG, "Données initialisées")
        }
    }

    fun loadExpense() {
        viewModelScope.launch {
            val expenseList = repository.getExpense()
            // Cette affectation notifie automatiquement les observers LiveData.
            _expense.value = expenseList

            Log.d(TAG, "Dépenses chargées : ${expenseList.size}")
        }
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        selectedDate = calendar.time
    }

    fun addExpense(title: String, amount: Double, note : String) {
        val cleanedTitle = title.trim()
        val cleanedAmount = amount.trim().toDoubleOrNull()
        val cleanedDate: Date = selectedDate
        val cleanedNote = note.trim()
        val cleanedCategory = selectedCategoryId

        if (cleanedTitle.isEmpty()) {
            Log.d(TAG, "Ajout refusé : titre vide")
            return
        }

        if (cleanedAmount == null || cleanedAmount <= 0.0) {
            Log.d(TAG, "Ajout refusé : montant vide")
            return
        }

        viewModelScope.launch {
            // Toutes les écritures Room passent par une coroutine pour ne pas bloquer l'UI.
            val createdExpense = repository.addExpense(
                title = cleanedTitle,
                amount = cleanedAmount,
                date = cleanedDate,
                note = cleanedNote,
                categoryId = cleanedCategory,
            )

            // Après modification en base, on recharge la source de vérité.
            loadExpense()

            Log.d(TAG, "Dépense ajoutée en base : $createdExpense")
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            val deleted = repository.deleteExpense(expense)

            if (deleted) {
                loadExpense()
                Log.d(TAG, "Dépense supprimée en base : $expense")
            } else {
                Log.d(TAG, "Suppression impossible : dépense introuvable")
            }
        }
    }

    fun clearAllExpense() {
        viewModelScope.launch {
            repository.clearAll()
            loadExpense()

            Log.d(TAG, "Toutes les dépenses ont été supprimées")
        }
    }
}