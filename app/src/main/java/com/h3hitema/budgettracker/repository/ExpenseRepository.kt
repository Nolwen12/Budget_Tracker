package com.h3hitema.budgettracker.repository

import com.h3hitema.budgettracker.data.local.ExpenseEntity
import com.h3hitema.budgettracker.data.local.CategoryEntity
import com.h3hitema.budgettracker.data.local.ExpenseDao
import com.h3hitema.budgettracker.data.local.CategoryDao
import java.sql.Date

class ExpenseRepository (
    private val expenseDao: ExpenseDao
    ) {
        suspend fun getExpense(): List<ExpenseEntity> {
            // Le Repository masque Room au reste de l'application.
            return expenseDao.getAllExpense()
        }

        suspend fun addExpense(title: String, amount: Double, date: Date, categoryId: Long, note: String): ExpenseEntity {
            // id = 0 indique à Room qu'il doit générer l'identifiant.
            val entityToInsert = ExpenseEntity(
                id = 0,
                title = title,
                amount = amount,
                date = date,
                note = note,
                categoryId = categoryId,
            )

            // insertExpense retourne l'ID SQLite généré automatiquement.
            val generatedId = expenseDao.insertExpense(entityToInsert).toInt()

            return ExpenseEntity(
                id = generatedId,
                title = title,
                amount = amount,
                date = date,
                note = note,
                categoryId = categoryId
            )
        }

        suspend fun deleteExpense(expense: ExpenseEntity): Boolean {
            val existingEntity = expenseDao.getExpenseById(expense.id)

            return if (existingEntity != null) {
                expenseDao.deleteExpense(existingEntity)
                true
            } else {
                false
            }
        }
    suspend fun deleteExpenseById(expenseId: Int): Boolean {
        val existingEntity = expenseDao.getExpenseById(expenseId)

        return if (existingEntity != null) {
            expenseDao.deleteExpenseId(expenseId)
            true
        } else {
            false
        }
    }

    suspend fun isEmpty(): Boolean {
        return expenseDao.countExpense() == 0
    }

    suspend fun count(): Int {
        return expenseDao.countExpense()
    }

    suspend fun clearAll() {
        expenseDao.deleteAllExpense()
    }
//vérifier si besoin de mettre des dépenses automatique
//    suspend fun insertInitialExpenseIfNeeded() {
//        if (expenseDao.countExpense() == 0) {
//            // Données de démonstration ajoutées uniquement au premier lancement.
//            expenseDao.insertExpense(
//                ExpenseEntity(
//                    title = "Pas de dépenses",
//                    amount = 0.00,
//                    date = 2026-12-12,
//                    category = "Pas de categorie",
//                    note = ""
//                )
//            )
//        }
//    }
}