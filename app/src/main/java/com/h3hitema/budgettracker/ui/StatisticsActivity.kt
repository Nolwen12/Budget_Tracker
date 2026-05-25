package com.h3hitema.budgettracker.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.core.content.ContextCompat
import com.h3hitema.budgettracker.R
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModel
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    // Variables UI
    private lateinit var tvCurrentPeriod: TextView
    private lateinit var tvTotalExpenses: TextView
    private lateinit var tvExpenseCount: TextView
    private lateinit var tvAverageExpense: TextView
    private lateinit var btnPreviousPeriod: Button
    private lateinit var btnNextPeriod: Button
    private lateinit var llChartContainer: LinearLayout
    private lateinit var llCategoriesList: LinearLayout

    // ViewModel
    private lateinit var expenseViewModel: ExpenseViewModel

    // Variables
    private var currentCalendar: Calendar = Calendar.getInstance()
    private val sdf = SimpleDateFormat("MMMM yyyy", Locale.FRENCH)

    companion object {
        private const val TAG = "StatisticsActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        bindViews()
        setupViewModel()
        configureListeners()
        updateStatistics()

        Log.d(TAG, "StatisticsActivity créée")
    }

    private fun bindViews() {
        tvCurrentPeriod = findViewById(R.id.tvCurrentPeriod)
        tvTotalExpenses = findViewById(R.id.tvTotalExpenses)
        tvExpenseCount = findViewById(R.id.tvExpenseCount)
        tvAverageExpense = findViewById(R.id.tvAverageExpense)
        btnPreviousPeriod = findViewById(R.id.btnPreviousPeriod)
        btnNextPeriod = findViewById(R.id.btnNextPeriod)
        llChartContainer = findViewById(R.id.llChartContainer)
        llCategoriesList = findViewById(R.id.llCategoriesList)
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

    private fun configureListeners() {
        btnPreviousPeriod.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, -1)
            updateStatistics()
        }

        btnNextPeriod.setOnClickListener {
            currentCalendar.add(Calendar.MONTH, 1)
            updateStatistics()
        }
    }

    private fun updateStatistics() {
        // Mettre à jour la période affichée
        tvCurrentPeriod.text = sdf.format(currentCalendar.time)

        // Récupérer les dépenses de la période
        val currentPeriodExpenses = getCurrentPeriodExpenses()
        
        // Calculer les statistiques
        val totalExpenses = currentPeriodExpenses.sumOf { it.amount }
        val expenseCount = currentPeriodExpenses.size
        val averageExpense = if (expenseCount > 0) totalExpenses / expenseCount else 0.0

        // Mettre à jour les TextViews
        tvTotalExpenses.text = String.format("%.2f €", totalExpenses)
        tvExpenseCount.text = expenseCount.toString()
        tvAverageExpense.text = String.format("%.2f €", averageExpense)

        // Mettre à jour le graphique et le détail par catégorie
        updateChart(currentPeriodExpenses)
        updateCategoriesList(currentPeriodExpenses)

        Log.d(TAG, "Statistiques mises à jour: $totalExpenses€, $expenseCount dépenses, moyenne: $averageExpense€")
    }

    private fun getCurrentPeriodExpenses(): List<com.h3hitema.budgettracker.model.Expense> {
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
            ),
            com.h3hitema.budgettracker.model.Expense(
                id = 4,
                title = "Restaurant",
                amount = 35.00,
                date = Calendar.getInstance().apply {
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.DAY_OF_MONTH, 22)
                }.time,
                note = "Resto avec amis",
                categoryId = 1L,
                categoryName = "Nourriture"
            ),
            com.h3hitema.budgettracker.model.Expense(
                id = 5,
                title = "Vêtements",
                amount = 89.99,
                date = Calendar.getInstance().apply {
                    set(Calendar.MONTH, currentMonth)
                    set(Calendar.YEAR, currentYear)
                    set(Calendar.DAY_OF_MONTH, 25)
                }.time,
                note = "Nouvelle chemise",
                categoryId = 3L,
                categoryName = "Shopping"
            )
        )
    }

    private fun updateChart(expenses: List<com.h3hitema.budgettracker.model.Expense>) {
        // Regrouper les dépenses par catégorie
        val categoryTotals = expenses.groupBy { it.categoryName }
            .mapValues { it.value.sumOf { expense -> expense.amount } }

        // Vider le conteneur du graphique
        llChartContainer.removeAllViews()

        if (categoryTotals.isEmpty()) return

        // Trouver le montant maximum pour l'échelle
        val maxAmount = categoryTotals.values.maxOrNull() ?: 0.0

        // Créer les barres du graphique
        categoryTotals.forEach { (categoryName, amount) ->
            val barContainer = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1.0f
                ).apply {
                    marginEnd = 4.dpToPx()
                }
            }

            // Barre de progression
            val barHeight = if (maxAmount > 0) (amount / maxAmount * 200).toInt() else 0
            val bar = View(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    barHeight
                )
                setBackgroundColor(getCategoryColor(categoryName))
            }

            // Label de la catégorie
            val label = TextView(this).apply {
                text = categoryName
                textSize = 10f
                setTextColor(getColor(android.R.color.black))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            // Montant
            val amountText = TextView(this).apply {
                text = String.format("%.0f€", amount)
                textSize = 10f
                setTextColor(getColor(android.R.color.black))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            }

            barContainer.addView(amountText)
            barContainer.addView(bar)
            barContainer.addView(label)
            llChartContainer.addView(barContainer)
        }
    }

    private fun updateCategoriesList(expenses: List<com.h3hitema.budgettracker.model.Expense>) {
        // Regrouper les dÃ©penses par catÃ©gorie
        val categoryStats = expenses.groupBy { it.categoryName }
            .map { (categoryName, categoryExpenses) ->
                CategoryStat(
                    name = categoryName,
                    total = categoryExpenses.sumOf { it.amount },
                    count = categoryExpenses.size,
                    average = if (categoryExpenses.isEmpty()) 0.0 else categoryExpenses.sumOf { it.amount } / categoryExpenses.size
                )
            }
            .sortedByDescending { it.total }

        // Vider la liste
        llCategoriesList.removeAllViews()

        // Ajouter chaque catégorie
        categoryStats.forEach { stat ->
            val categoryView = createCategoryView(stat)
            llCategoriesList.addView(categoryView)
        }
    }

    private fun createCategoryView(stat: CategoryStat): View {
        val container = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(0, 8.dpToPx(), 0, 8.dpToPx())
        }

        // Couleur de la catégorie
        val colorIndicator = View(this).apply {
            layoutParams = LinearLayout.LayoutParams(4.dpToPx(), LinearLayout.LayoutParams.MATCH_PARENT).apply {
                marginEnd = 12.dpToPx()
            }
            setBackgroundColor(getCategoryColor(stat.name))
        }

        // Informations de la catégorie
        val infoLayout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        }

        val nameText = TextView(this).apply {
            text = stat.name
            textSize = 14f
            setTextColor(getColor(android.R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        val detailsText = TextView(this).apply {
            text = "${stat.count} dépenses • Moyenne: ${String.format("%.2f€", stat.average)}"
            textSize = 12f
            setTextColor(getColor(android.R.color.darker_gray))
        }

        // Total
        val totalText = TextView(this).apply {
            text = String.format("%.2f€", stat.total)
            textSize = 14f
            setTextColor(getColor(android.R.color.black))
            setTypeface(null, android.graphics.Typeface.BOLD)
        }

        infoLayout.addView(nameText)
        infoLayout.addView(detailsText)

        container.addView(colorIndicator)
        container.addView(infoLayout)
        container.addView(totalText)

        return container
    }

    private fun getCategoryColor(categoryName: String): Int {
        return when (categoryName.lowercase()) {
            "nourriture" -> ContextCompat.getColor(this, R.color.category_food)
            "transport" -> ContextCompat.getColor(this, R.color.category_transport)
            "shopping" -> ContextCompat.getColor(this, R.color.category_shopping)
            "santé" -> ContextCompat.getColor(this, R.color.category_health)
            "loisir" -> ContextCompat.getColor(this, R.color.purple_500)
            else -> ContextCompat.getColor(this, R.color.category_other)
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * resources.displayMetrics.density).toInt()
    }

    data class CategoryStat(
        val name: String,
        val total: Double,
        val count: Int,
        val average: Double
    )
}