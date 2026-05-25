package com.h3hitema.budgettracker.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.h3hitema.budgettracker.R
import com.h3hitema.budgettracker.data.local.AppDatabase
import com.h3hitema.budgettracker.model.Expense
import com.h3hitema.budgettracker.repository.CategoryRepository
import com.h3hitema.budgettracker.repository.ExpenseRepository
import com.h3hitema.budgettracker.ui.adapter.ExpenseAdapter
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModel
import com.h3hitema.budgettracker.viewmodel.ExpenseViewModelFactory
import com.h3hitema.budgettracker.ui.ExpenseAddActivity
import kotlin.collections.emptyList
import kotlin.jvm.java

class ExpenseActivity : AppCompatActivity() {

    // Variables
    private lateinit var rvExpenses: RecyclerView
    private lateinit var btnAddExpense: Button
    private lateinit var tvEmpty: TextView
    private lateinit var expenseAdapter: ExpenseAdapter
    private lateinit var expenseViewModel: ExpenseViewModel

    companion object {
        private const val TAG = "ExpenseActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        bindViews()
        setupRecyclerView()
        setupViewModel()
        configureListeners()
        observeViewModel()

        Log.d(TAG, "ExpenseActivity créée")
    }

    private fun bindViews() {
        rvExpenses = findViewById(R.id.rvExpenses)
        btnAddExpense = findViewById(R.id.btnAddExpense)
        tvEmpty = findViewById(R.id.tvEmpty)
    }

    private fun setupRecyclerView() {
        expenseAdapter = ExpenseAdapter(emptyList<Expense>()) { expense ->
            // Clic sur une dépense → modifier
            val intent = Intent(this, ExpenseAddActivity::class.java)
            intent.putExtra("expenseId", expense.id)
            startActivity(intent)
        }

        rvExpenses.adapter = expenseAdapter
        rvExpenses.layoutManager = LinearLayoutManager(this)
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
        btnAddExpense.setOnClickListener {
            val intent = Intent(this, ExpenseAddActivity::class.java)
            startActivity(intent)
        }
    }

    private fun observeViewModel() {
        expenseViewModel.expenses.observe(this) { expenses ->
            expenseAdapter.submitExpenses(expenses)
            tvEmpty.visibility = if (expenses.isEmpty()) View.VISIBLE else View.GONE
        }
    }
}