package com.h3hitema.budgettracker.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.h3hitema.budgettracker.R
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModel
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAddActivity : AppCompatActivity() {

    // Variables UI
    private lateinit var etTitle: TextInputEditText
    private lateinit var etAmount: TextInputEditText
    private lateinit var etCategory: MaterialAutoCompleteTextView
    private lateinit var etDate: TextInputEditText
    private lateinit var etNote: TextInputEditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    // ViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    // Variables
    private var expenseId: Int? = null
    private val categories = listOf("Nourriture", "Transport", "Shopping", "Santé", "Loisir", "Autre")
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)

    companion object {
        private const val TAG = "ExpenseAddActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_add)

        // Récupérer l'ID si modification
        expenseId = intent.getIntExtra("expenseId", -1).takeIf { it > 0 }

        bindViews()
        setupViewModel()
        setupCategoryDropdown()
        setupDatePicker()
        configureListeners()

        // Si modification, charger les données
        expenseId?.let { loadExpenseData(it) }

        Log.d(TAG, "ExpenseAddActivity créée${if (expenseId != null) " (mode modification)" else " (mode ajout)"}")
    }

    private fun bindViews() {
        etTitle = findViewById(R.id.etTitle)
        etAmount = findViewById(R.id.etAmount)
        etCategory = findViewById(R.id.etCategory)
        etDate = findViewById(R.id.etDate)
        etNote = findViewById(R.id.etNote)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
    }

    private fun setupViewModel() {
        val database = AppDatabase.getDatabase(this)
        val expenseRepository = ExpenseRepository(database.expenseDao())
        val categoryRepository = CategoryRepository(database.categoryDao())

        expenseViewModel = ViewModelProvider(
            this,
            ExpenseViewModelFactory(expenseRepository, categoryRepository)
        )[ExpenseViewModel::class.java]
    }

    private fun setupCategoryDropdown() {
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        etCategory.setAdapter(adapter)
        
        // Catégorie par défaut
        if (etCategory.text.isNullOrEmpty()) {
            etCategory.setText(categories.first(), false)
        }
    }

    private fun setupDatePicker() {
        // Date par défaut = aujourd'hui
        etDate.setText(sdf.format(Date()))
        expenseViewModel.selectedDate = Date()

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = expenseViewModel.selectedDate

            DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    expenseViewModel.onDateSelected(year, month, dayOfMonth)
                    etDate.setText(sdf.format(expenseViewModel.selectedDate))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun configureListeners() {
        btnSave.setOnClickListener {
            saveExpense()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadExpenseData(expenseId: Int) {
        // TODO: Charger les données de la dépense existante
        // Pour l'instant, on reste en mode ajout
        Log.d(TAG, "Mode modification non encore implémenté pour l'ID: $expenseId")
    }

    private fun saveExpense() {
        val title = etTitle.text.toString()
        val amountStr = etAmount.text.toString()
        val categoryStr = etCategory.text.toString()
        val note = etNote.text.toString()

        // Validation
        if (title.isEmpty()) {
            etTitle.error = "Le titre est obligatoire"
            etTitle.requestFocus()
            return
        }

        if (amountStr.isEmpty()) {
            etAmount.error = "Le montant est obligatoire"
            etAmount.requestFocus()
            return
        }

        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            etAmount.error = "Montant invalide"
            etAmount.requestFocus()
            return
        }

        if (categoryStr.isEmpty()) {
            etCategory.error = "La catégorie est obligatoire"
            etCategory.requestFocus()
            return
        }

        // Trouver l'ID de la catégorie (pour l'instant, on utilise une simple correspondance)
        val categoryId = when (categoryStr.lowercase()) {
            "nourriture" -> 1L
            "transport" -> 2L
            "shopping" -> 3L
            "santé" -> 4L
            "loisir" -> 5L
            else -> 6L
        }

        // Sauvegarder via ViewModel
        expenseViewModel.selectedCategoryId = categoryId
        expenseViewModel.addExpense(title, amountStr, note)
        
        Toast.makeText(this, "Dépense ajoutée avec succès", Toast.LENGTH_SHORT).show()
        Log.d(TAG, "Dépense sauvegardée: $title, $amount€, $categoryStr")
        
        // Retour à l'écran précédent
        finish()
    }
}