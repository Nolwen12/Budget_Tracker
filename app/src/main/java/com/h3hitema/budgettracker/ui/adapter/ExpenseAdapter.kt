package com.h3hitema.budgettracker.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.h3hitema.budgettracker.R
import com.h3hitema.budgettracker.data.local.ExpenseEntity
import com.h3hitema.budgettracker.model.Expense
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseAdapter (
    private var expenses: List<Expense>,
    private val onExpenseClicked: (Expense) -> Unit,
) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

    class ExpenseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Le ViewHolder garde les références des vues d'une ligne RecyclerView.
        val llCategoryBackground : LinearLayout = itemView.findViewById(R.id.llCategoryBackground)
        val tvCategory : TextView = itemView.findViewById<TextView>(R.id.tvCategory)
        val tvDate : TextView = itemView.findViewById<TextView>(R.id.tvDate)
        val tvTitle : TextView = itemView.findViewById<TextView>(R.id.tvTitle)
        val tvAmount : TextView = itemView.findViewById<TextView>(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense, parent, false)

        return ExpenseViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
        val expense = expenses[position]

        // Les lignes sont recyclées : on renseigne toujours titre, priorité et statut.
        // Titre et montant
        holder.tvTitle.text = expense.title
        holder.tvAmount.text = "-${expense.amount}€"

        // Date formatée
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH)
        holder.tvDate.text = sdf.format(expense.date)

        // Catégorie
        holder.tvCategory.text = expense.categoryName  // ← on verra ça juste après

        // Couleur de fond selon la catégorie
        val color = getCategoryColor(holder.itemView.context, expense.categoryName)
        holder.llCategoryBackground.setBackgroundColor(color)

        // Clic sur la carte → modifier
        holder.itemView.setOnClickListener {
            onExpenseClicked(expense)
        }
    }

    override fun getItemCount(): Int {
        return expenses.size
    }

    // Couleur selon la catégorie
    private fun getCategoryColor(context: Context, categoryName: String): Int {
        return when (categoryName.lowercase()) {
            "nourriture" -> ContextCompat.getColor(context, R.color.category_food)
            "transport"  -> ContextCompat.getColor(context, R.color.category_transport)
            "shopping"   -> ContextCompat.getColor(context, R.color.category_shopping)
            "santé"      -> ContextCompat.getColor(context, R.color.category_health)
            else         -> ContextCompat.getColor(context, R.color.category_other)
        }
    }

    fun submitExpenses(newExpenses: List<Expense>) {
        // La liste vient de Room via le ViewModel : l'Adapter ne décide pas des données.
        expenses = newExpenses
        notifyDataSetChanged()
    }
}