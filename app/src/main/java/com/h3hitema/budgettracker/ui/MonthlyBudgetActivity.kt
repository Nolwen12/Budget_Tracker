package com.h3hitema.budgettracker.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.h3hitema.budgettracker.R
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModel
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class MonthlyBudgetActivity : AppCompatActivity() {

    // Variables UI
    private lateinit var tvMonthlyBudget: TextView
    private lateinit var tvMonthlySpent: TextView
    private lateinit var tvMonthlyRemaining: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgressPercentage: TextView
    private lateinit var tvCurrentMonth: TextView
    private lateinit var btnPreviousMonth: Button
    private lateinit var btnNextMonth: Button
    private lateinit var btnEditBudget: Button

    // ViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    // Variables
    private var currentCalendar: Calendar = Calendar.getInstance()
    private var monthlyBudget: Double = 1000.0 // Budget par défaut
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)

    companion object {
        private const val TAG = "MonthlyBudgetActivity"
        private const val PREFS_NAME = "BudgetPrefs"
        private const val BUDGET_KEY = "monthly_budget"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_budget)

        bindViews()
        setupViewModel()
        loadBudgetFromPrefs()
        configureListeners()
        updateDisplay()

        Log.d(TAG, "MonthlyBudgetActivity créée")
    }

    private fun bindViews() {
        tvMonthlyBudget = findViewById(R.id.tvMonthlyBudget)
        tvMonthlySpent = findViewById(R.id.tvMonthlySpent)
        tvMonthlyRemaining = findViewById(R.id.tvMonthlyRemaining)
        progressBar = findViewById(R.id.progressBar)
        tvProgressPercentage = findViewById(R.id.tvProgressPercentage)
        tvCurrentMonth = findViewById(R.id.tvCurrentMonth)
        btnPreviousMonth = findViewById(R.id.btnPreviousMonth)
        btnNextMonth = findViewById(R.id.btnNextMonth)
        btnEditBudget = findViewById(R.id.btnEditBudget)
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

    private fun loadBudgetFromPrefs() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        monthlyBudget = prefs.getFloat(BUDGET_KEY, 1000.0f).toDouble()
    }

    private fun saveBudgetToPrefs() {
        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        prefs.edit().putFloat(BUDGET_KEY, monthlyBudget.toFloat()).apply()
    }

    private fun configureListeners() {
        btnPreviousMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateDisplay()
        }

        btnNextMonth.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateDisplay()
        }

        btnEditBudget.setOnClickListener {
            showEditBudgetDialog()
        }
    }

    private fun updateDisplay() {
        // Mettre à jour le mois affiché
        tvCurrentMonth.text = sdf.format(currentCalendar.time)

        // Calculer les dépenses du mois en cours
        val currentMonthExpenses = getCurrentMonthExpenses()
        val totalSpent = currentMonthExpenses.sumOf { it.amount }
        val remaining = monthlyBudget - totalSpent
        val percentage = if (monthlyBudget > 0) (totalSpent / monthlyBudget * 100).toInt() else 0

        // Mettre à jour les TextViews
        tvMonthlyBudget.text = String.format("%.2f €", monthlyBudget)
        tvMonthlySpent.text = String.format("%.2f €", totalSpent)
        tvMonthlyRemaining.text = String.format("%.2f €", remaining)

        // Mettre à jour la barre de progression
        progressBar.progress = percentage.coerceIn(0, 100)
        tvProgressPercentage.text = "$percentage% du budget utilisé"

        // Changer la couleur du reste selon le contexte
        if (remaining < 0) {
            tvMonthlyRemaining.setTextColor(getColor(R.color.design_default_color_error))
        } else if (remaining < monthlyBudget * 0.2) {
            tvMonthlyRemaining.setTextColor(getColor(android.R.color.holo_orange_dark))
        } else {
            tvMonthlyRemaining.setTextColor(getColor(R.color.design_default_color_primary))
        }

        Log.d(TAG, "Budget mis à jour: $monthlyBudget€, dépensé: $totalSpent€, reste: $remaining€")
    }

    private fun getCurrentMonthExpenses(): List<com.h3hitema.budgettracker.model.Expense> {
        // Pour l'instant, on simule les données
        // TODO: Récupérer les vraies dépenses depuis le ViewModel
        val currentMonth = currentCalendar.get(Calendar.MONTH)
        val currentYear = currentCalendar.get(Calendar.YEAR)
        
        // Simulation de dépenses pour le test
        return listOf(
            com.h3hitema.budgettracker.model.Expense(
                id = 1,
                title = "Courses Carrefour",
                amount = 150.75,
                date = Calendar.getInstance().apply {
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.DAY_OF_MONTH, 5)
                }.time,
                note = "Courses hebdomadaires",
                categoryId = 1L,
                categoryName = "Nourriture"
            ),
            com.h3hitema.budgettracker.model.Expense(
                id = 2,
                title = "Essence",
                amount = 60.00,
                date = Calendar.getInstance().apply {
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.DAY_OF_MONTH, 12)
                }.time,
                note = "Plein d'essence",
                categoryId = 2L,
                categoryName = "Transport"
            ),
            com.h3hitema.budgettracker.model.Expense(
                id = 3,
                title = "Pharmacie",
                amount = 25.50,
                date = Calendar.getInstance().apply {
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.DAY_OF_MONTH, 18)
                }.time,
                note = "Médicaments",
                categoryId = 4L,
                categoryName = "Santé"
            )
        )
    }

    private fun showEditBudgetDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Modifier le budget mensuel")

        // Créer un EditText pour le nouveau budget
        val input = TextInputEditText(this)
        input.hint = "Nouveau budget (€)"
        input.setText(monthlyBudget.toString())
        builder.setView(input)

        builder.setPositiveButton("Enregistrer") { dialog, _ ->
            val newBudgetStr = input.text.toString()
            val newBudget = newBudgetStr.toDoubleOrNull()

            if (newBudget != null && newBudget > 0) {
                monthlyBudget = newBudget
                saveBudgetToPrefs()
                updateDisplay()
                Toast.makeText(this, "Budget mis à jour", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "Budget modifié: $monthlyBudget€")
            } else {
                Toast.makeText(this, "Budget invalide", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Annuler") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }
}