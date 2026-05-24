package com.h3hitema.budgettracker.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.data.local.CategoryEntity
import com.h3hitema.budgettracker.model.Category
import com.h3hitema.budgettracker.repository.CategoryRepository
import kotlinx.coroutines.launch
import java.util.Date

class CategoryViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "BudgetTracker"
    }

    private val repository: CategoryRepository

    // LiveData observée par l'Activity : chaque nouvelle valeur déclenche un rendu UI.
    private val _category = MutableLiveData<List<CategoryEntity>>(emptyList())
    val category: LiveData<List<CategoryEntity>> = _category

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        val database = AppDatabase.getDatabase(application)
        repository = CategoryRepository(database.categoryDao())

        // L'initialisation reste dans le ViewModel pour ne pas charger l'Activity.
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            _isLoading.value = true

            loadCategory()

            // Quand isLoading repasse à false, l'écran peut masquer le chargement.
            _isLoading.value = false

            Log.d(CategoryViewModel.Companion.TAG, "Catégorie initialisées")
        }
    }

    fun loadCategory() {
        viewModelScope.launch {
            val categoryList = repository.getCategory()
            // Cette affectation notifie automatiquement les observers LiveData.
            _category.value = categoryList

            Log.d(CategoryViewModel.Companion.TAG, "Catégorie chargées : ${categoryList.size}")
        }
    }

    fun addCategory(wording: String) {
        val cleanedWording = wording.trim()

        if (cleanedWording.isEmpty()) {
            Log.d(CategoryViewModel.Companion.TAG, "Ajout refusé : titre vide")
            return
        }

        viewModelScope.launch {
            // Toutes les écritures Room passent par une coroutine pour ne pas bloquer l'UI.
            val createdCategory = repository.addCategory(
                wording = cleanedWording
            )

            // Après modification en base, on recharge la source de vérité.
            loadCategory()

            Log.d(CategoryViewModel.Companion.TAG, "Catégorie ajoutée en base : $createdCategory")
        }
    }

    fun deleteCategory(category: CategoryEntity) {
        viewModelScope.launch {
            val deleted = repository.deleteCategory(category)

            if (deleted) {
                loadCategory()
                Log.d(CategoryViewModel.Companion.TAG, "Catégorie supprimée en base : $category")
            } else {
                Log.d(CategoryViewModel.Companion.TAG, "Suppression impossible : catégorie introuvable")
            }
        }
    }

    fun clearAllCategory() {
        viewModelScope.launch {
            repository.clearAll()
            loadCategory()

            Log.d(CategoryViewModel.Companion.TAG, "Toutes les catégories ont été supprimées")
        }
    }
    
}