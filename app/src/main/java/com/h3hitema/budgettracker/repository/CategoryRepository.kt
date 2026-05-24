package com.h3hitema.budgettracker.repository

import com.h3hitema.budgettracker.data.local.CategoryEntity
import com.h3hitema.budgettracker.data.local.CategoryDao

class CategoryRepository (
    private val categoryDao: CategoryDao
) {
    suspend fun getCategorys(): List<CategoryEntity> {
        // Le Repository masque Room au reste de l'application.
        return categoryDao.getAllCategory()
    }

    suspend fun addCategory(title: String, wording: String): CategoryEntity {
        // id = 0 indique à Room qu'il doit générer l'identifiant.
        val entityToInsert = CategoryEntity(
            id = 0,
            wording = wording
        )

        // insertCategory retourne l'ID SQLite généré automatiquement.
        val generatedId = categoryDao.insertCategory(entityToInsert).toInt()

        return CategoryEntity(
            id = generatedId,
            wording = wording
        )
    }

    suspend fun deleteCategory(category: CategoryEntity): Boolean {
        val existingEntity = categoryDao.getCategoryById(category.id)

        return if (existingEntity != null) {
            categoryDao.deleteCategory(existingEntity)
            true
        } else {
            false
        }
    }

    suspend fun deleteCategoryById(categoryId: Int): Boolean {
        val existingEntity = categoryDao.getCategoryById(categoryId)

        return if (existingEntity != null) {
            categoryDao.deleteCategoryId(categoryId)
            true
        } else {
            false
        }
    }

    suspend fun isEmpty(): Boolean {
        return categoryDao.countCategory() == 0
    }

    suspend fun count(): Int {
        return categoryDao.countCategory()
    }

    suspend fun clearAll() {
        categoryDao.deleteAllCategory()
    }

    suspend fun insertInitialCategoryIfNeeded() {
        if (categoryDao.countCategory() == 0) {
            // Données de démonstration ajoutées uniquement au premier lancement.
            categoryDao.insertCategory(
                CategoryEntity(
                    wording = "Alimentation"
                )
            )

            categoryDao.insertCategory(
                CategoryEntity(
                    wording = "Logement"
                )
            )

            categoryDao.insertCategory(
                CategoryEntity(
                    wording = "Transport"
                )
            )

            categoryDao.insertCategory(
                CategoryEntity(
                    wording = "Loisirs"
                )
            )


        }
    }

    suspend fun getCategoryWithExpenses(categoryId: Long) =
        categoryDao.getCategoryWithExpenses(categoryId)
}